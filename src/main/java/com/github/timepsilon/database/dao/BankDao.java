package com.github.timepsilon.database.dao;

import com.github.timepsilon.Core;
import com.github.timepsilon.database.PostgresHelper;
import com.github.timepsilon.database.entity.BalanceHistoryPoint;
import com.github.timepsilon.database.entity.BankEntry;
import com.github.timepsilon.database.pending.PendingWriteQueue;
import com.github.timepsilon.database.pending.PendingWritesStore;

import javax.annotation.Nullable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BankDao {

    private static final String CREATE_TABLE = """
            CREATE TABLE IF NOT EXISTS banks (
                player UUID NOT NULL,
                username TEXT NOT NULL,
                time TIMESTAMPTZ(3) NOT NULL,
                money INTEGER NOT NULL,
                PRIMARY KEY (player, time)
            )
            """;

    private static final String MIGRATE_USERNAME = """
            ALTER TABLE banks ADD COLUMN IF NOT EXISTS username TEXT NOT NULL DEFAULT 'unknown'
            """;

    private static final String MIGRATE_TIMESTAMP = """
            DO $$
            BEGIN
                IF EXISTS (
                    SELECT 1 FROM information_schema.columns
                    WHERE table_schema = current_schema()
                      AND table_name = 'banks'
                      AND column_name = 'time'
                      AND data_type = 'date'
                ) THEN
                    ALTER TABLE banks
                        ALTER COLUMN time TYPE TIMESTAMPTZ(3)
                        USING (time::timestamp AT TIME ZONE 'UTC');
                END IF;
            END $$
            """;

    private static final String UPSERT = """
            INSERT INTO banks
            (player, username, time, money)
            VALUES (?, ?, ?, ?)
            ON CONFLICT (player, time)
            DO UPDATE SET
            money = excluded.money,
            username = excluded.username
            """;

    private static final String FETCH_HISTORY = """
            SELECT username, time, money
            FROM banks
            WHERE time >= ?
            ORDER BY time ASC
            LIMIT ?
            """;

    private final PendingWriteQueue<BankEntry> pending = PendingWritesStore.get().banks();

    private @Nullable Connection connection;
    private @Nullable PreparedStatement upsertStatement;
    private @Nullable ConnectionSupplier connectionSupplier = PostgresHelper::open;

    public void connect() {
        connect(connectionSupplier);
    }

    public void connect(ConnectionSupplier supplier) {
        connectionSupplier = supplier;
        try {
            if (connection != null && !connection.isClosed()) return;
            connection = supplier.get();
            Core.LOGGER.debug("Connected to PostgreSQL (table={}).", BankEntry.TABLE_NAME);
            createTable();
            tryFlushPending();
        } catch (SQLException e) {
            Core.LOGGER.error("Error loading bank accounts database!", e);
            disconnect();
        }
    }

    public void createTable() {
        if (connection == null) return;
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(CREATE_TABLE);
            stmt.execute(MIGRATE_USERNAME);
            stmt.execute(MIGRATE_TIMESTAMP);
            upsertStatement = connection.prepareStatement(UPSERT);
        } catch (Exception e) {
            Core.LOGGER.error("Error creating bank accounts table", e);
            disconnect();
        }
    }

    public void upsertAll(Collection<BankEntry> entries) {
        if (entries.isEmpty()) return;
        ensureConnected();
        if (!isReady()) {
            pending.enqueueAll(entries);
            Core.LOGGER.debug(
                    "Pending write enqueued (no connection): table={}, batch={}, queueSize={}",
                    BankEntry.TABLE_NAME, entries.size(), pending.size()
            );
            PendingWritesStore.get().persistToDisk();
            return;
        }
        if (!executeBatch(entries)) {
            pending.enqueueAll(entries);
            Core.LOGGER.debug(
                    "Pending write enqueued (write failed): table={}, batch={}, queueSize={}",
                    BankEntry.TABLE_NAME, entries.size(), pending.size()
            );
        }
    }

    public List<BalanceHistoryPoint> fetchPlayerBalanceHistory(Instant since, int limit) {
        if (limit <= 0) {
            return List.of();
        }
        ensureConnected();
        if (connection == null) {
            return List.of();
        }
        try (PreparedStatement statement = connection.prepareStatement(FETCH_HISTORY)) {
            statement.setTimestamp(1, Timestamp.from(since));
            statement.setInt(2, limit);
            try (ResultSet resultSet = statement.executeQuery()) {
                List<BalanceHistoryPoint> points = new ArrayList<>();
                while (resultSet.next()) {
                    points.add(new BalanceHistoryPoint(
                            resultSet.getString("username"),
                            resultSet.getTimestamp("time").toInstant(),
                            resultSet.getInt("money")
                    ));
                }
                return List.copyOf(points);
            }
        } catch (SQLException e) {
            Core.LOGGER.error("Failed to fetch bank balance history", e);
            if (PostgresHelper.isConnectionError(e)) {
                disconnect();
            }
            return List.of();
        }
    }

    public boolean tryFlushPending() {
        if (pending.isEmpty()) return true;
        ensureConnected();
        if (!isReady()) return false;

        List<BankEntry> toFlush = pending.drainAll();
        Core.LOGGER.info("Flushing {} pending bank write(s).", toFlush.size());
        if (executeBatch(toFlush)) {
            PendingWritesStore.get().persistToDisk();
            return true;
        }
        pending.enqueueAll(toFlush);
        PendingWritesStore.get().persistToDisk();
        return false;
    }

    public void flushAndClose() {
        tryFlushPending();
        try {
            if (upsertStatement != null) {
                int[] results = upsertStatement.executeBatch();
                Core.LOGGER.debug(
                        "SQL batch flushed on close: table={}, count={}",
                        BankEntry.TABLE_NAME, results.length
                );
                upsertStatement.clearBatch();
            }
        } catch (SQLException e) {
            Core.LOGGER.error("Failed to flush banks to database!", e);
        }
        disconnect();
    }

    private void ensureConnected() {
        if (isReady()) {
            try {
                if (connection.isClosed()) {
                    disconnect();
                }
            } catch (SQLException e) {
                disconnect();
            }
        }
        if (!isReady()) {
            connect(connectionSupplier);
        }
    }

    private boolean isReady() {
        return connection != null && upsertStatement != null;
    }

    private boolean executeBatch(Collection<BankEntry> entries) {
        if (upsertStatement == null) return false;
        try {
            for (BankEntry entry : entries) {
                Core.LOGGER.debug(
                        "SQL upsert batch: table={}, player={}, username={}, time={}, money={}",
                        BankEntry.TABLE_NAME, entry.player(), entry.username(), entry.time(), entry.money()
                );
                entry.bindTo(upsertStatement);
                upsertStatement.addBatch();
            }
            int[] results = upsertStatement.executeBatch();
            Core.LOGGER.debug(
                    "SQL batch executed: table={}, count={}",
                    BankEntry.TABLE_NAME, results.length
            );
            upsertStatement.clearBatch();
            return true;
        } catch (SQLException e) {
            Core.LOGGER.error("Couldn't update banks database: ", e);
            if (PostgresHelper.isConnectionError(e)) {
                Core.LOGGER.warn("PostgreSQL connection lost (table={}), will retry pending writes.", BankEntry.TABLE_NAME);
            }
            disconnect();
            return false;
        }
    }

    private void disconnect() {
        try {
            if (upsertStatement != null) {
                upsertStatement.close();
                upsertStatement = null;
            }
            if (connection != null && !connection.isClosed()) {
                connection.close();
                connection = null;
                Core.LOGGER.debug("Disconnected from PostgreSQL (table={}).", BankEntry.TABLE_NAME);
            }
        } catch (SQLException e) {
            Core.LOGGER.error("Error closing bank accounts database!", e);
        }
    }

    @FunctionalInterface
    public interface ConnectionSupplier {
        Connection get() throws SQLException;
    }
}

package com.github.timepsilon.database.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.github.timepsilon.database.PostgresHelper;
import com.github.timepsilon.database.entity.SctTransactionEntry;
import com.github.timepsilon.database.pending.PendingWriteQueue;
import com.github.timepsilon.database.pending.PendingWritesStore;

import javax.annotation.Nullable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.List;

public class SctTransactionDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(SctTransactionDao.class);

    private static final String CREATE_TABLE = """
            CREATE TABLE IF NOT EXISTS sct_transaction (
                time TIMESTAMPTZ(3) NOT NULL,
                player UUID NOT NULL,
                username TEXT NOT NULL,
                item TEXT NOT NULL,
                amount INTEGER NOT NULL,
                money INTEGER NOT NULL,
                PRIMARY KEY (time, player, item)
            )
            """;

    private static final String MIGRATE_USERNAME = """
            ALTER TABLE sct_transaction ADD COLUMN IF NOT EXISTS username TEXT NOT NULL DEFAULT 'unknown'
            """;

    private static final String MIGRATE_TIMESTAMP = """
            DO $$
            BEGIN
                IF EXISTS (
                    SELECT 1 FROM information_schema.columns
                    WHERE table_schema = current_schema()
                      AND table_name = 'sct_transaction'
                      AND column_name = 'hour'
                ) THEN
                    ALTER TABLE sct_transaction RENAME COLUMN hour TO time;
                    ALTER TABLE sct_transaction
                        ALTER COLUMN time TYPE TIMESTAMPTZ(3)
                        USING to_timestamp(time * 3600) AT TIME ZONE 'UTC';
                END IF;
            END $$
            """;

    private static final String UPSERT = """
            INSERT INTO sct_transaction
            (time, player, username, item, amount, money)
            VALUES (?, ?, ?, ?, ?, ?)
            ON CONFLICT (time, player, item)
            DO UPDATE SET
            amount = sct_transaction.amount + excluded.amount,
            money = sct_transaction.money + excluded.money,
            username = excluded.username
            """;

    private final PendingWriteQueue<SctTransactionEntry> pending = PendingWritesStore.get().sctTransactions();

    private @Nullable Connection connection;
    private @Nullable PreparedStatement upsertStatement;
    private @Nullable BankDao.ConnectionSupplier connectionSupplier = PostgresHelper::open;

    public void connect() {
        connect(connectionSupplier);
    }

    public void connect(BankDao.ConnectionSupplier supplier) {
        connectionSupplier = supplier;
        try {
            if (connection != null && !connection.isClosed()) return;
            connection = supplier.get();
            LOGGER.debug("Connected to PostgreSQL (table={}).", SctTransactionEntry.TABLE_NAME);
            createTable();
            tryFlushPending();
        } catch (SQLException e) {
            LOGGER.error("Error loading SCT transaction database!", e);
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
            LOGGER.error("Error creating SCT transaction table.", e);
            disconnect();
        }
    }

    public void upsertAll(Collection<SctTransactionEntry> entries) {
        if (entries.isEmpty()) return;
        ensureConnected();
        if (!isReady()) {
            pending.enqueueAll(entries);
            LOGGER.debug(
                    "Pending write enqueued (no connection): table={}, batch={}, queueSize={}",
                    SctTransactionEntry.TABLE_NAME, entries.size(), pending.size()
            );
            PendingWritesStore.get().persistToDisk();
            return;
        }
        if (!executeBatch(entries)) {
            pending.enqueueAll(entries);
            LOGGER.debug(
                    "Pending write enqueued (write failed): table={}, batch={}, queueSize={}",
                    SctTransactionEntry.TABLE_NAME, entries.size(), pending.size()
            );
        }
    }

    public boolean tryFlushPending() {
        if (pending.isEmpty()) return true;
        ensureConnected();
        if (!isReady()) return false;

        List<SctTransactionEntry> toFlush = pending.drainAll();
        LOGGER.info("Flushing {} pending SCT transaction write(s).", toFlush.size());
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
                LOGGER.debug(
                        "SQL batch flushed on close: table={}, count={}",
                        SctTransactionEntry.TABLE_NAME, results.length
                );
                upsertStatement.clearBatch();
            }
        } catch (SQLException e) {
            LOGGER.error("Failed to flush transactions to database!", e);
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

    private boolean executeBatch(Collection<SctTransactionEntry> entries) {
        if (upsertStatement == null) return false;
        try {
            for (SctTransactionEntry entry : entries) {
                LOGGER.debug(
                        "SQL upsert batch: table={}, player={}, username={}, time={}, item={}, amount={}, money={}",
                        SctTransactionEntry.TABLE_NAME, entry.player(), entry.username(), entry.time(),
                        entry.itemId(), entry.amount(), entry.moneyAsFloat()
                );
                entry.bindTo(upsertStatement);
                upsertStatement.addBatch();
            }
            int[] results = upsertStatement.executeBatch();
            LOGGER.debug(
                    "SQL batch executed: table={}, count={}",
                    SctTransactionEntry.TABLE_NAME, results.length
            );
            upsertStatement.clearBatch();
            return true;
        } catch (SQLException e) {
            LOGGER.error("Failed to send transactions to database!", e);
            if (PostgresHelper.isConnectionError(e)) {
                LOGGER.warn(
                        "PostgreSQL connection lost (table={}), will retry pending writes.",
                        SctTransactionEntry.TABLE_NAME
                );
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
                LOGGER.debug("Disconnected from PostgreSQL (table={}).", SctTransactionEntry.TABLE_NAME);
            }
        } catch (SQLException e) {
            LOGGER.error("Error closing SCT transaction database!", e);
        }
    }
}

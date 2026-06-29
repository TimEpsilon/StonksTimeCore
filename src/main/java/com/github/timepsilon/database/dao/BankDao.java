package com.github.timepsilon.database.dao;

import com.github.timepsilon.Core;
import com.github.timepsilon.database.PostgresHelper;
import com.github.timepsilon.database.entity.BankEntry;

import javax.annotation.Nullable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;

public class BankDao {

    private static final String CREATE_TABLE = """
            CREATE TABLE IF NOT EXISTS banks (
                player UUID NOT NULL,
                username TEXT NOT NULL,
                time DATE NOT NULL,
                money INTEGER NOT NULL,
                PRIMARY KEY (player, time)
            )
            """;

    private static final String MIGRATE_USERNAME = """
            ALTER TABLE banks ADD COLUMN IF NOT EXISTS username TEXT NOT NULL DEFAULT 'unknown'
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

    private @Nullable Connection connection;
    private @Nullable PreparedStatement upsertStatement;

    public void connect() {
        connect(PostgresHelper::open);
    }

    public void connect(ConnectionSupplier supplier) {
        try {
            if (connection != null && !connection.isClosed()) return;
            connection = supplier.get();
            Core.LOGGER.debug("Connected to PostgreSQL (table={}).", BankEntry.TABLE_NAME);
        } catch (SQLException e) {
            Core.LOGGER.error("Error loading bank accounts database!", e);
        }
    }

    public void createTable() {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(CREATE_TABLE);
            stmt.execute(MIGRATE_USERNAME);
            upsertStatement = connection.prepareStatement(UPSERT);
        } catch (Exception e) {
            Core.LOGGER.error("Error creating bank accounts table", e);
        }
    }

    public void upsertAll(Collection<BankEntry> entries) {
        if (upsertStatement == null) return;
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
        } catch (SQLException e) {
            Core.LOGGER.error("Couldn't update banks database: ", e);
        }
    }

    public void flushAndClose() {
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

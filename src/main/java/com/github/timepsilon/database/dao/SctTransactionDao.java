package com.github.timepsilon.database.dao;

import com.github.timepsilon.database.PostgresHelper;
import com.github.timepsilon.database.entity.SctTransactionEntry;

import javax.annotation.Nullable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SctTransactionDao {

    private static final Logger LOGGER = Logger.getLogger(SctTransactionDao.class.getName());

    private static final String CREATE_TABLE = """
            CREATE TABLE IF NOT EXISTS sct_transaction (
                hour BIGINT NOT NULL,
                player UUID NOT NULL,
                item TEXT NOT NULL,
                amount INTEGER NOT NULL,
                money INTEGER NOT NULL,
                PRIMARY KEY (hour, player, item)
            )
            """;

    private static final String UPSERT = """
            INSERT INTO sct_transaction
            (hour, player, item, amount, money)
            VALUES (?, ?, ?, ?, ?)
            ON CONFLICT (hour, player, item)
            DO UPDATE SET
            amount = sct_transaction.amount + excluded.amount,
            money = sct_transaction.money + excluded.money
            """;

    private @Nullable Connection connection;
    private @Nullable PreparedStatement upsertStatement;

    public void connect() {
        connect(PostgresHelper::open);
    }

    public void connect(BankDao.ConnectionSupplier supplier) {
        try {
            if (connection != null && !connection.isClosed()) return;
            connection = supplier.get();
            LOGGER.info("Connected to PostgreSQL (sct_transaction).");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error loading SCT transaction database!", e);
        }
    }

    public void createTable() {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(CREATE_TABLE);
            upsertStatement = connection.prepareStatement(UPSERT);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error creating SCT transaction table.", e);
        }
    }

    public void upsertAll(Collection<SctTransactionEntry> entries) {
        if (upsertStatement == null) return;
        try {
            for (SctTransactionEntry entry : entries) {
                entry.bindTo(upsertStatement);
                upsertStatement.addBatch();
            }
            upsertStatement.executeBatch();
            upsertStatement.clearBatch();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to send transactions to database!", e);
        }
    }

    public void flushAndClose() {
        try {
            if (upsertStatement != null) {
                upsertStatement.executeBatch();
                upsertStatement.clearBatch();
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to flush transactions to database!", e);
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
                LOGGER.info("Disconnected from SCT transaction database.");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error closing SCT transaction database!", e);
        }
    }
}

package com.github.timepsilon.database;

import com.github.timepsilon.Core;
import com.github.timepsilon.utils.TimeUtils;
import dev.ithundxr.createnumismatics.content.backend.BankAccount;
import dev.ithundxr.createnumismatics.content.backend.BankSavedData;

import java.sql.*;
import java.util.Map;
import java.util.UUID;

public class MoneyDatabase extends AbstractDatabase {

    private static final MoneyDatabase DATABASE = new MoneyDatabase();

    private MoneyDatabase() {}

    public void saveBanks() {
        try {
            for (Map.Entry<UUID,BankAccount> entry : BankSavedData.load(server).getAccounts().entrySet()) {
                long time = TimeUtils.getCurrentMinute();
                byte[] uuid = TimeUtils.UUIDToBytes(entry.getKey());
                int money = entry.getValue().getBalance();

                statement.setBytes(1, uuid);
                statement.setLong(2, time);
                statement.setInt(3, money);
                statement.addBatch();
            }
            statement.executeBatch();
            statement.clearBatch();
        } catch (SQLException e) {
            Core.LOGGER.error("Couldn't Update Banks Database : ", e);
        }
    }

    protected void createTables() {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("""
            CREATE TABLE IF NOT EXISTS banks (
                player BLOB NOT NULL,
                time INTEGER NOT NULL,
                money INTEGER NOT NULL,
                PRIMARY KEY (player,time)
            )
            """);

            // Prepared statement
            statement = connection.prepareStatement("""
            INSERT INTO banks
            (player, time, money)
            VALUES (?, ?, ?)
            ON CONFLICT (player, time)
            DO UPDATE SET
            money = excluded.money;
            """);
        } catch (Exception e) {
            Core.LOGGER.error("Error Creating {} Database", getDatabaseName(), e);
        }
    }

    @Override
    public String getDatabaseName() {
        return "BankAccounts";
    }

    public static MoneyDatabase getDatabase() {
        return DATABASE;
    }
}

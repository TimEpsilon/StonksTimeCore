package com.github.timepsilon.database;

import com.github.timepsilon.Core;
import com.github.timepsilon.utils.TimeUtils;
import dev.ithundxr.createnumismatics.content.backend.BankAccount;
import dev.ithundxr.createnumismatics.content.backend.BankSavedData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;

import javax.annotation.Nullable;
import java.nio.file.Path;
import java.sql.*;
import java.util.Map;
import java.util.UUID;

import static com.github.timepsilon.utils.FileManager.makeServerSideDirectory;

public class MoneyDatabase {

    private static final MoneyDatabase DATABASE = new MoneyDatabase();

    private @Nullable Connection connection;
    private @Nullable MinecraftServer server;
    private @Nullable PreparedStatement statement;

    private MoneyDatabase() {}

    public void load(MinecraftServer server) {
        this.server = server;
        connect();
        createTables();
    }

    public void unload() {
        // Flush buffer
        try {
            statement.executeBatch();
            statement.clearBatch();
        } catch (SQLException e) {
            Core.LOGGER.error("Failed to Flush Banks to Database!", e);
        }

        // Close connection
        this.disconnect();
    }

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

    private void connect() {
        try {
            if (connection != null && !connection.isClosed()) return;

            // Connection
            Path database = makeServerSideDirectory(server).resolve("BankAccounts.db");
            connection = DriverManager.getConnection(
                    "jdbc:sqlite:" + database.toAbsolutePath()
            );

            // Statement
            try (Statement statement = connection.createStatement()) {
                statement.execute("PRAGMA journal_mode=WAL");
                statement.execute("PRAGMA synchronous=NORMAL");
            }
            Core.LOGGER.info("Connected to SCT Bank Accounts Database.");

        } catch (final Exception e) {
            Core.LOGGER.error("Error Loading Bank Accounts Database!", e);
        }
    }

    private void disconnect() {
        try {
            if (statement != null) {
                statement.close();
                statement = null;
            }

            if (connection != null && !connection.isClosed()) {
                connection.close();
                server = null;
                connection = null;

                Core.LOGGER.info("Disconnected from Bank Accounts Database.");
            }

        } catch (final SQLException e) {
            Core.LOGGER.error("Error Closing Bank Accounts Database!", e);
        }
    }

    private void createTables() {
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
            Core.LOGGER.error("Error Creating Bank Accounts Database", e);
        }
    }

    public static MoneyDatabase getDatabase() {
        return DATABASE;
    }
}

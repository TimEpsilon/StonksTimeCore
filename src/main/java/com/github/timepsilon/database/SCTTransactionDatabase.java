package com.github.timepsilon.database;

import com.github.timepsilon.Core;
import com.github.timepsilon.utils.TimeUtils;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;

import javax.annotation.Nullable;
import java.nio.file.Path;
import java.sql.*;
import java.util.Date;

import static com.github.timepsilon.utils.FileManager.makeServerSideDirectory;

public class SCTTransactionDatabase {

    private static SCTTransactionDatabase DATABASE = new SCTTransactionDatabase();

    private Connection connection;
    private @Nullable MinecraftServer server;
    private PreparedStatement statement;

    public SCTTransactionDatabase() {}

    public void load(MinecraftServer server) {
        this.server = server;
        this.connect();
        this.createTables();
    }

    public void unload() {
        // Flush buffer

    }

    public void sendTransaction(ServerPlayer player, Item item, int amount, int money) {
        try {
            statement.setLong(1, TimeUtils.getCurrentHour());
            statement.setBytes(2, TimeUtils.UUIDToBytes(player.getUUID()));
            statement.setString(3, item.toString());
            statement.setInt(4, amount);
            statement.setInt(5, money*1000);

            statement.addBatch();
        } catch (SQLException e) {
            Core.LOGGER.error("Couldn't Update Database : ", e);
        }
    }

    private void connect() {
        try {
            Class.forName("org.sqlite.JDBC");

            if (this.connection != null && !this.connection.isClosed()) return;

            // Connection
            Path database = makeServerSideDirectory(server).resolve("SCTTransaction.db");
            connection = DriverManager.getConnection(
                    "jdbc:sqlite:" + database.toAbsolutePath()
            );

            // Statement
            Statement statement = connection.createStatement();
            statement.execute("PRAGMA journal_mode=WAL");
            statement.execute("PRAGMA synchronous=NORMAL");


            Core.LOGGER.info("Connected to SCT Transaction Database.");


        } catch (final Exception e) {
            Core.LOGGER.error("Error Loading SCT Transaction Database!", e);
        }
    }

    private void createTables() {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("""
            CREATE TABLE IF NOT EXISTS sct_transaction (
                hour INTEGER NOT NULL,
                player BLOB NOT NULL,
                item TEXT NOT NULL,
                amount INTEGER NOT NULL,
                money INTEGER NOT NULL,
                PRIMARY KEY (hour, player, item)
            )
            """);

            // Prepared statement
            statement = connection.prepareStatement("""
            INSERT INTO sct_transaction
            (hour, player, item, amount, money)
            VALUES (?, ?, ?, ?, ?)
            ON CONFLICT (hour, player, item)
            DO UPDATE SET
            amount = amount + excluded.amount,
            money = money + excluded.money;
            """);
        } catch (Exception e) {
            Core.LOGGER.error("Error Creating SCT Database.", e);
        }
    }

    public Connection getConnection() {
        return this.connection;
    }

    public static SCTTransactionDatabase getDatabase() {
        return DATABASE;
    }
}

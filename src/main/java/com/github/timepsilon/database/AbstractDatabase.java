package com.github.timepsilon.database;

import com.github.timepsilon.Core;
import net.minecraft.server.MinecraftServer;

import javax.annotation.Nullable;
import java.nio.file.Path;
import java.sql.*;

import static com.github.timepsilon.utils.FileManager.makeServerSideDirectory;

public abstract class AbstractDatabase {

    protected @Nullable Connection connection;
    protected @Nullable MinecraftServer server;
    protected @Nullable PreparedStatement statement;

    public AbstractDatabase() {}

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
            Core.LOGGER.error("Failed to Flush to Database!", e);
        }

        // Close connection
        this.disconnect();
    }

    private void connect() {
        try {
            if (connection != null && !connection.isClosed()) return;

            // Connection
            Path database = makeServerSideDirectory(server).resolve(getDatabaseName() + ".db");
            connection = DriverManager.getConnection(
                    "jdbc:sqlite:" + database.toAbsolutePath()
            );

            // Statement
            try (Statement statement = connection.createStatement()) {
                statement.execute("PRAGMA journal_mode=WAL");
                statement.execute("PRAGMA synchronous=NORMAL");
            }
            Core.LOGGER.info("Connected to {} Database.", getDatabaseName());

        } catch (final Exception e) {
            Core.LOGGER.error("Error Loading {} Database!", getDatabaseName(), e);
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

                Core.LOGGER.info("Disconnected from {} Database.", getDatabaseName());
            }

        } catch (final SQLException e) {
            Core.LOGGER.error("Error Closing {} Database!", getDatabaseName(), e);
        }
    }

    protected abstract void createTables();

    public abstract String getDatabaseName();

}

package com.github.timepsilon.database;

import com.github.timepsilon.utils.FileManager;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * Helper for the embedded SQLite analytics database (bank snapshots + SCT transactions).
 * <p>
 * Everything lives in a single file inside the world save, so the mod runs identically
 * in singleplayer and on a dedicated server without any external database to set up.
 * Grafana can read the same file on a server (see {@code grafana/}).
 */
public final class SqliteHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(SqliteHelper.class);

    /** Single database file shared by every analytics table. */
    public static final String DATABASE_FILE = "stonkstime.db";

    /**
     * Fixed-width ISO-8601 UTC formatter ({@code 2025-06-20T08:00:00.000Z}).
     * <p>
     * Timestamps are stored as TEXT: the fixed width makes lexical ordering equivalent to
     * chronological ordering ({@code MAX(time)}, {@code ORDER BY time}), and the RFC3339
     * shape is parsed natively by Grafana's SQLite datasource.
     */
    public static final DateTimeFormatter ISO_UTC =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZone(ZoneOffset.UTC);

    private SqliteHelper() {}

    /** Resolves the analytics database file inside the server's world directory. */
    public static Path databaseFile(MinecraftServer server) {
        return FileManager.makeServerSideDirectory(server).resolve(DATABASE_FILE);
    }

    public static Connection open(Path databaseFile) throws SQLException {
        String jdbcUrl = "jdbc:sqlite:" + databaseFile.toAbsolutePath();
        LOGGER.debug("Opening SQLite connection: url={}", jdbcUrl);
        Connection connection = DriverManager.getConnection(jdbcUrl);
        try (Statement statement = connection.createStatement()) {
            // WAL lets Grafana read while the game writes; busy_timeout avoids spurious lock errors.
            statement.execute("PRAGMA journal_mode=WAL");
            statement.execute("PRAGMA synchronous=NORMAL");
            statement.execute("PRAGMA busy_timeout=5000");
        }
        LOGGER.debug("SQLite connection opened: url={}", jdbcUrl);
        return connection;
    }

    /** ISO-8601 UTC text representation used for every stored timestamp. */
    public static String toIso(Instant instant) {
        return ISO_UTC.format(instant);
    }

    public static boolean isConnectionError(SQLException exception) {
        String message = exception.getMessage();
        return message != null && (
                message.contains("closed") ||
                message.contains("Connection is closed")
        );
    }
}

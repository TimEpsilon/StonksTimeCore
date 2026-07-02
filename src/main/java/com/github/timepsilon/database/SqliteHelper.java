package com.github.timepsilon.database;

import com.github.timepsilon.utils.FileManager;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
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
     * Non-WAL snapshot copy that Grafana reads. Grafana (especially in Docker on
     * Windows) cannot open the live WAL database over a bind mount because the
     * {@code -shm} shared-memory file cannot be mmap'd; a plain rollback-journal
     * copy sidesteps that entirely.
     */
    public static final String GRAFANA_SNAPSHOT_FILE = "stonkstime-export.db";

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

    /** Resolves the Grafana snapshot file (next to the live database). */
    public static Path grafanaSnapshotFile(MinecraftServer server) {
        return FileManager.makeServerSideDirectory(server).resolve(GRAFANA_SNAPSHOT_FILE);
    }

    /**
     * Writes a consistent, non-WAL copy of {@code liveDatabase} to {@code snapshot} for Grafana.
     * <p>
     * {@code VACUUM INTO} runs inside a read transaction (safe against the live writer) and emits a
     * fresh rollback-journal database — no {@code -wal}/{@code -shm} sidecars. The copy is written to
     * a temp file then moved into place so a reader never observes a half-written file.
     */
    public static void writeGrafanaSnapshot(Path liveDatabase, Path snapshot) throws SQLException, IOException {
        Path tmp = snapshot.resolveSibling(snapshot.getFileName() + ".tmp");
        Files.deleteIfExists(tmp);
        String jdbcUrl = "jdbc:sqlite:" + liveDatabase.toAbsolutePath();
        try (Connection connection = DriverManager.getConnection(jdbcUrl);
             Statement statement = connection.createStatement()) {
            statement.execute("PRAGMA busy_timeout=5000");
            // VACUUM INTO takes a SQL string literal, not a bind parameter; escape single quotes.
            String target = tmp.toAbsolutePath().toString().replace("'", "''");
            statement.execute("VACUUM INTO '" + target + "'");
        }
        try {
            Files.move(tmp, snapshot, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } catch (AtomicMoveNotSupportedException e) {
            Files.move(tmp, snapshot, StandardCopyOption.REPLACE_EXISTING);
        }
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

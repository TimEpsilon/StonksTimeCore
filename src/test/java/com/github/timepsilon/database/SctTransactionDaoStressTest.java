package com.github.timepsilon.database;

import com.github.timepsilon.database.dao.SctTransactionDao;
import com.github.timepsilon.database.entity.SctTransactionEntry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SctTransactionDaoStressTest {

    private static final int TRANSACTIONS_PER_SECOND = 200;
    private static final long MAX_DURATION_MS = 5_000;

    @TempDir
    Path tempDir;

    @Test
    void upsert200TransactionsWithinOneSecond() throws SQLException {
        Path databaseFile = tempDir.resolve("stress.db");

        SctTransactionDao dao = new SctTransactionDao();
        dao.connect(() -> SqliteHelper.open(databaseFile));
        dao.createTable();

        UUID player = UUID.randomUUID();
        Instant hourStart = Instant.parse("2025-06-20T08:00:00.000Z");

        long startNanos = System.nanoTime();
        for (int i = 0; i < TRANSACTIONS_PER_SECOND; i++) {
            SctTransactionEntry entry = new SctTransactionEntry(
                    hourStart,
                    player,
                    "StressPlayer",
                    "minecraft:stress_item_" + i,
                    1,
                    100 + i,
                    10
            );
            dao.upsertAll(List.of(entry));
        }
        long elapsedMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNanos);

        dao.flushAndClose();

        assertEquals(TRANSACTIONS_PER_SECOND, countRows(databaseFile),
                "All stress-test transactions must be persisted");
        assertTrue(elapsedMs <= MAX_DURATION_MS,
                () -> "Expected " + TRANSACTIONS_PER_SECOND + " upserts within " + MAX_DURATION_MS
                        + " ms, took " + elapsedMs + " ms");
    }

    private static int countRows(Path databaseFile) throws SQLException {
        try (Connection connection = SqliteHelper.open(databaseFile);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(
                     "SELECT COUNT(*) FROM " + SctTransactionEntry.TABLE_NAME)) {
            resultSet.next();
            return resultSet.getInt(1);
        }
    }
}

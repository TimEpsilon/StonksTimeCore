package com.github.timepsilon.database;

import com.github.timepsilon.database.entity.BankEntry;
import com.github.timepsilon.database.entity.SctTransactionEntry;
import com.github.timepsilon.database.pending.PendingWriteQueue;
import com.github.timepsilon.utils.TimeCore;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PendingWriteQueueTest {

    private static final UUID PLAYER = UUID.fromString("00000000-0000-0000-0000-000000000001");

    @Test
    void bankQueueKeepsLatestSnapshotPerPlayerAndTimestamp() {
        PendingWriteQueue<BankEntry> queue = new PendingWriteQueue<>(
                BankEntry.TABLE_NAME,
                entry -> entry.player() + "|" + entry.time(),
                (existing, incoming) -> incoming
        );

        Instant snapshotTime = Instant.parse("2026-06-29T12:00:00.000Z");
        queue.enqueueAll(List.of(new BankEntry(PLAYER, "Alice", snapshotTime, 100)));
        queue.enqueueAll(List.of(new BankEntry(PLAYER, "Alice", snapshotTime, 250)));

        assertEquals(1, queue.size());
        assertEquals(250, queue.drainAll().getFirst().money());
    }

    @Test
    void sctQueueMergesAmountsForSameKey() {
        PendingWriteQueue<SctTransactionEntry> queue = new PendingWriteQueue<>(
                SctTransactionEntry.TABLE_NAME,
                entry -> TimeCore.truncateToHour(entry.time()) + "|" + entry.player() + "|" + entry.itemId(),
                (existing, incoming) -> new SctTransactionEntry(
                        existing.time(),
                        existing.player(),
                        incoming.username(),
                        existing.itemId(),
                        existing.amount() + incoming.amount(),
                        existing.storedMoney() + incoming.storedMoney()
                )
        );

        Instant hourStart = Instant.parse("2026-06-29T12:00:00.000Z");
        queue.enqueueAll(List.of(new SctTransactionEntry(
                hourStart, PLAYER, "Alice", "minecraft:gold", 2, 1000
        )));
        queue.enqueueAll(List.of(new SctTransactionEntry(
                hourStart, PLAYER, "Alice", "minecraft:gold", 3, 500
        )));

        assertEquals(1, queue.size());
        SctTransactionEntry merged = queue.drainAll().getFirst();
        assertEquals(5, merged.amount());
        assertEquals(1500, merged.storedMoney());
    }

    @Test
    void drainClearsQueue() {
        PendingWriteQueue<BankEntry> queue = new PendingWriteQueue<>(
                BankEntry.TABLE_NAME,
                entry -> entry.player() + "|" + entry.time(),
                (existing, incoming) -> incoming
        );

        queue.enqueueAll(List.of(new BankEntry(PLAYER, "Alice", Instant.parse("2026-06-29T12:00:00.000Z"), 10)));
        assertEquals(1, queue.drainAll().size());
        assertTrue(queue.isEmpty());
    }
}

package com.github.timepsilon.database;

import com.github.timepsilon.database.entity.BankEntry;
import com.github.timepsilon.database.entity.SctTransactionEntry;
import com.github.timepsilon.database.pending.PendingWriteQueue;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PendingWriteQueueTest {

    private static final UUID PLAYER = UUID.fromString("00000000-0000-0000-0000-000000000001");

    @Test
    void bankQueueKeepsLatestSnapshotPerPlayerAndDay() {
        PendingWriteQueue<BankEntry> queue = new PendingWriteQueue<>(
                BankEntry.TABLE_NAME,
                entry -> entry.player() + "|" + entry.time(),
                (existing, incoming) -> incoming
        );

        LocalDate day = LocalDate.of(2026, 6, 29);
        queue.enqueueAll(List.of(new BankEntry(PLAYER, "Alice", day, 100)));
        queue.enqueueAll(List.of(new BankEntry(PLAYER, "Alice", day, 250)));

        assertEquals(1, queue.size());
        assertEquals(250, queue.drainAll().getFirst().money());
    }

    @Test
    void sctQueueMergesAmountsForSameKey() {
        PendingWriteQueue<SctTransactionEntry> queue = new PendingWriteQueue<>(
                SctTransactionEntry.TABLE_NAME,
                entry -> entry.hour() + "|" + entry.player() + "|" + entry.itemId(),
                (existing, incoming) -> new SctTransactionEntry(
                        existing.hour(),
                        existing.player(),
                        incoming.username(),
                        existing.itemId(),
                        existing.amount() + incoming.amount(),
                        existing.storedMoney() + incoming.storedMoney()
                )
        );

        queue.enqueueAll(List.of(new SctTransactionEntry(
                42L, PLAYER, "Alice", "minecraft:gold", 2, 1000
        )));
        queue.enqueueAll(List.of(new SctTransactionEntry(
                42L, PLAYER, "Alice", "minecraft:gold", 3, 500
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

        queue.enqueueAll(List.of(new BankEntry(PLAYER, "Alice", LocalDate.of(2026, 6, 29), 10)));
        assertEquals(1, queue.drainAll().size());
        assertTrue(queue.isEmpty());
    }
}

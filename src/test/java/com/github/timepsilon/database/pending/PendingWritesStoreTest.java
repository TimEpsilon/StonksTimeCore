package com.github.timepsilon.database.pending;

import com.github.timepsilon.database.entity.BankEntry;
import com.github.timepsilon.database.entity.SctTransactionEntry;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PendingWritesStoreTest {

    private static final UUID PLAYER = UUID.fromString("00000000-0000-0000-0000-000000000001");

    private final PendingWritesStore store = PendingWritesStore.get();

    @AfterEach
    void drainQueues() {
        store.banks().drainAll();
        store.sctTransactions().drainAll();
    }

    @Test
    void bankQueueKeepsLatestSnapshotPerPlayerAndTimestamp() {
        Instant snapshotTime = Instant.parse("2026-06-29T12:00:00.000Z");
        store.banks().enqueueAll(List.of(new BankEntry(PLAYER, "Alice", snapshotTime, 100)));
        store.banks().enqueueAll(List.of(new BankEntry(PLAYER, "Alice", snapshotTime, 250)));

        assertEquals(1, store.banks().size());
        assertEquals(250, store.banks().snapshot().getFirst().money());
        assertTrue(store.hasPending());
        assertEquals(1, store.pendingCount());
    }

    @Test
    void sctQueueMergesAmountsForSameHourPlayerAndItem() {
        Instant hourStart = Instant.parse("2026-06-29T12:00:00.000Z");
        Instant laterInHour = Instant.parse("2026-06-29T12:45:00.000Z");

        store.sctTransactions().enqueueAll(List.of(new SctTransactionEntry(
                hourStart, PLAYER, "Alice", "minecraft:gold", 2, 1000, 10
        )));
        store.sctTransactions().enqueueAll(List.of(new SctTransactionEntry(
                laterInHour, PLAYER, "AliceUpdated", "minecraft:gold", 3, 500, 10
        )));

        assertEquals(1, store.sctTransactions().size());
        SctTransactionEntry merged = store.sctTransactions().snapshot().getFirst();
        assertEquals(5, merged.amount());
        assertEquals(1500, merged.storedMoney());
        assertEquals(hourStart, merged.time());
        assertEquals("AliceUpdated", merged.username());
    }

    @Test
    void sctQueueKeepsSeparateEntriesForDifferentItems() {
        Instant hourStart = Instant.parse("2026-06-29T12:00:00.000Z");

        store.sctTransactions().enqueueAll(List.of(
                new SctTransactionEntry(hourStart, PLAYER, "Alice", "minecraft:gold", 1, 100, 10),
                new SctTransactionEntry(hourStart, PLAYER, "Alice", "minecraft:iron", 2, 200, 10)
        ));

        assertEquals(2, store.sctTransactions().size());
        assertEquals(2, store.pendingCount());
    }

    @Test
    void hasPendingIsFalseWhenQueuesAreEmpty() {
        assertFalse(store.hasPending());
        assertEquals(0, store.pendingCount());
    }
}

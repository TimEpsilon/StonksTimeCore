package com.github.timepsilon.database.pending;

import com.github.timepsilon.Core;
import com.github.timepsilon.database.entity.BankEntry;
import com.github.timepsilon.database.entity.SctTransactionEntry;
import com.github.timepsilon.utils.TimeUtils;
import com.github.timepsilon.utils.FileManager;
import net.minecraft.server.MinecraftServer;

import javax.annotation.Nullable;
import java.nio.file.Path;
import java.util.List;

public final class PendingWritesStore {

    private static final PendingWritesStore INSTANCE = new PendingWritesStore();

    private final PendingWriteQueue<BankEntry> banks = new PendingWriteQueue<>(
            BankEntry.TABLE_NAME,
            entry -> entry.player() + "|" + entry.time(),
            (existing, incoming) -> incoming
    );

    private final PendingWriteQueue<SctTransactionEntry> sctTransactions = new PendingWriteQueue<>(
            SctTransactionEntry.TABLE_NAME,
            entry -> TimeUtils.truncateToHour(entry.time()) + "|" + entry.player() + "|" + entry.itemId(),
            (existing, incoming) -> new SctTransactionEntry(
                    existing.time(),
                    existing.player(),
                    incoming.username(),
                    existing.itemId(),
                    existing.amount() + incoming.amount(),
                    existing.storedMoney() + incoming.storedMoney()
            )
    );

    private @Nullable MinecraftServer server;
    private boolean loadedFromDisk;

    private PendingWritesStore() {}

    public static PendingWritesStore get() {
        return INSTANCE;
    }

    public PendingWriteQueue<BankEntry> banks() {
        return banks;
    }

    public PendingWriteQueue<SctTransactionEntry> sctTransactions() {
        return sctTransactions;
    }

    public void bindServer(MinecraftServer server) {
        this.server = server;
        if (!loadedFromDisk) {
            loadFromDisk();
            loadedFromDisk = true;
        }
    }

    public void clearServer() {
        persistToDisk();
        server = null;
        loadedFromDisk = false;
    }

    public void loadFromDisk() {
        if (server == null) return;

        PendingWritesSnapshot snapshot = FileManager.readFileOnWorld(
                PendingWritesSnapshot.FILE_NAME,
                PendingWritesSnapshot.class,
                server
        );
        if (snapshot == null) return;

        if (!snapshot.banks.isEmpty()) {
            banks.replaceAll(snapshot.banks.stream().map(PendingWritesSnapshot.BankEntryDto::toEntry).toList());
            Core.LOGGER.info(
                    "Loaded {} pending bank write(s) from disk.",
                    snapshot.banks.size()
            );
        }
        if (!snapshot.sctTransaction.isEmpty()) {
            sctTransactions.replaceAll(snapshot.sctTransaction.stream()
                    .map(PendingWritesSnapshot.SctTransactionEntryDto::toEntry)
                    .toList());
            Core.LOGGER.info(
                    "Loaded {} pending SCT transaction write(s) from disk.",
                    snapshot.sctTransaction.size()
            );
        }
    }

    public void persistToDisk() {
        if (server == null) return;

        PendingWritesSnapshot snapshot = new PendingWritesSnapshot();
        for (BankEntry entry : banks.snapshot()) {
            snapshot.banks.add(PendingWritesSnapshot.BankEntryDto.from(entry));
        }
        for (SctTransactionEntry entry : sctTransactions.snapshot()) {
            snapshot.sctTransaction.add(PendingWritesSnapshot.SctTransactionEntryDto.from(entry));
        }

        FileManager.writeFileOnWorld(PendingWritesSnapshot.FILE_NAME, snapshot, server);
        Core.LOGGER.debug(
                "Persisted pending writes: banks={}, sctTransaction={}",
                snapshot.banks.size(),
                snapshot.sctTransaction.size()
        );
    }

    public boolean hasPending() {
        return !banks.isEmpty() || !sctTransactions.isEmpty();
    }

    public int pendingCount() {
        return banks.size() + sctTransactions.size();
    }
}

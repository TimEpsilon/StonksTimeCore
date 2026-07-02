package com.github.timepsilon.database;

import com.github.timepsilon.config.SqlStatsGate;
import com.github.timepsilon.database.dao.BankDao;
import com.github.timepsilon.database.entity.BalanceHistoryPoint;
import com.github.timepsilon.database.entity.BankEntry;
import com.github.timepsilon.utils.TimeCore;
import dev.ithundxr.createnumismatics.content.backend.BankAccount;
import dev.ithundxr.createnumismatics.content.backend.BankSavedData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import javax.annotation.Nullable;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MoneyDatabase {

    private static final MoneyDatabase DATABASE = new MoneyDatabase();

    private final BankDao dao = new BankDao();
    private @Nullable MinecraftServer server;

    private MoneyDatabase() {}

    public void load(MinecraftServer server) {
        if (!SqlStatsGate.isEnabled()) return;
        this.server = server;
        Path databaseFile = SqliteHelper.databaseFile(server);
        dao.connect(() -> SqliteHelper.open(databaseFile));
        dao.tryFlushPending();
    }

    public void unload() {
        if (!SqlStatsGate.isEnabled()) return;
        dao.flushAndClose();
        server = null;
    }

    public void flushPending() {
        if (!SqlStatsGate.isEnabled()) return;
        dao.tryFlushPending();
    }

    public List<BalanceHistoryPoint> fetchBalanceHistory(Instant since, int limit) {
        return dao.fetchPlayerBalanceHistory(since, limit);
    }

    public void saveBanks() {
        if (!SqlStatsGate.isEnabled() || server == null) return;

        // Only snapshot online players so "connected players over time" is meaningful:
        // an offline player's balance doesn't change, so there is nothing to record.
        // One shared timestamp per cycle so every player in this cycle lands on the same `time`.
        Instant now = TimeCore.getCurrentInstant();
        Map<UUID, BankAccount> accounts = BankSavedData.load(server).getAccounts();
        List<BankEntry> entries = new ArrayList<>();
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            BankAccount account = accounts.get(player.getUUID());
            if (account == null) continue;
            entries.add(BankEntry.snapshot(
                    player.getUUID(),
                    player.getGameProfile().getName(),
                    account.getBalance(),
                    now
            ));
        }
        if (entries.isEmpty()) return;
        dao.upsertAll(entries);
    }

    public static MoneyDatabase getDatabase() {
        return DATABASE;
    }
}

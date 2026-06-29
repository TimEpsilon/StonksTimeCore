package com.github.timepsilon.database;

import com.github.timepsilon.database.dao.BankDao;
import com.github.timepsilon.database.entity.BalanceHistoryPoint;
import com.github.timepsilon.database.entity.BankEntry;
import com.mojang.authlib.GameProfile;
import dev.ithundxr.createnumismatics.content.backend.BankAccount;
import dev.ithundxr.createnumismatics.content.backend.BankSavedData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import javax.annotation.Nullable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class MoneyDatabase {

    private static final MoneyDatabase DATABASE = new MoneyDatabase();

    private final BankDao dao = new BankDao();
    private @Nullable MinecraftServer server;

    private MoneyDatabase() {}

    public void load(MinecraftServer server) {
        this.server = server;
        dao.connect();
        dao.tryFlushPending();
    }

    public void unload() {
        dao.flushAndClose();
        server = null;
    }

    public void flushPending() {
        dao.tryFlushPending();
    }

    public List<BalanceHistoryPoint> fetchBalanceHistory(Instant since, int limit) {
        return dao.fetchPlayerBalanceHistory(since, limit);
    }

    public void saveBanks() {
        if (server == null) return;

        List<BankEntry> entries = new ArrayList<>();
        for (Map.Entry<UUID, BankAccount> entry : BankSavedData.load(server).getAccounts().entrySet()) {
            UUID playerId = entry.getKey();
            entries.add(BankEntry.snapshot(playerId, resolveUsername(playerId), entry.getValue().getBalance()));
        }
        dao.upsertAll(entries);
    }

    private String resolveUsername(UUID playerId) {
        ServerPlayer online = server.getPlayerList().getPlayer(playerId);
        if (online != null) {
            return online.getGameProfile().getName();
        }
        Optional<GameProfile> profile = server.getProfileCache().get(playerId);
        return profile.map(GameProfile::getName).orElseGet(() -> playerId.toString());
    }

    public static MoneyDatabase getDatabase() {
        return DATABASE;
    }
}

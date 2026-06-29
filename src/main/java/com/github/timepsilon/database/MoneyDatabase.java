package com.github.timepsilon.database;

import com.github.timepsilon.database.dao.BankDao;
import com.github.timepsilon.database.entity.BankEntry;
import dev.ithundxr.createnumismatics.content.backend.BankAccount;
import dev.ithundxr.createnumismatics.content.backend.BankSavedData;
import net.minecraft.server.MinecraftServer;

import javax.annotation.Nullable;
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
        this.server = server;
        dao.connect();
        dao.createTable();
    }

    public void unload() {
        dao.flushAndClose();
        server = null;
    }

    public void saveBanks() {
        if (server == null) return;

        List<BankEntry> entries = new ArrayList<>();
        for (Map.Entry<UUID, BankAccount> entry : BankSavedData.load(server).getAccounts().entrySet()) {
            entries.add(BankEntry.snapshot(entry.getKey(), entry.getValue().getBalance()));
        }
        dao.upsertAll(entries);
    }

    public static MoneyDatabase getDatabase() {
        return DATABASE;
    }
}

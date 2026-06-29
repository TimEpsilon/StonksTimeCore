package com.github.timepsilon.database;

import com.github.timepsilon.database.dao.SctTransactionDao;
import com.github.timepsilon.database.entity.SctTransactionEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SCTTransactionDatabase {

    private static final SCTTransactionDatabase DATABASE = new SCTTransactionDatabase();

    private final SctTransactionDao dao = new SctTransactionDao();

    private SCTTransactionDatabase() {}

    public void load(MinecraftServer server) {
        dao.connect();
        dao.createTable();
    }

    public void unload() {
        dao.flushAndClose();
    }

    public void sendTransactions(ServerPlayer player, Map<Item, Integer> amountMap, Map<Item, Float> moneyMap) {
        List<SctTransactionEntry> entries = new ArrayList<>();
        for (Map.Entry<Item, Integer> entry : amountMap.entrySet()) {
            Item item = entry.getKey();
            entries.add(SctTransactionEntry.from(
                    player,
                    item,
                    entry.getValue(),
                    moneyMap.getOrDefault(item, 0.0f)
            ));
        }
        dao.upsertAll(entries);
    }

    public static SCTTransactionDatabase getDatabase() {
        return DATABASE;
    }
}

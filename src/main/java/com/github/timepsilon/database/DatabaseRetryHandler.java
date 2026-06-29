package com.github.timepsilon.database;

import com.github.timepsilon.Core;
import com.github.timepsilon.config.SqlStatsGate;
import com.github.timepsilon.database.pending.PendingWritesStore;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

@EventBusSubscriber(modid = Core.MODID)
public final class DatabaseRetryHandler {

    private static final int RETRY_INTERVAL_TICKS = 1200;

    private DatabaseRetryHandler() {}

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        if (!SqlStatsGate.isEnabled()) return;
        if (event.getServer().getTickCount() % RETRY_INTERVAL_TICKS != 0) return;
        if (!PendingWritesStore.get().hasPending()) return;

        Core.LOGGER.debug(
                "Retrying {} pending database write(s)...",
                PendingWritesStore.get().pendingCount()
        );
        MoneyDatabase.getDatabase().flushPending();
        SCTTransactionDatabase.getDatabase().flushPending();
    }
}

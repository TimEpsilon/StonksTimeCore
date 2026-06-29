package com.github.timepsilon.database;

import com.github.timepsilon.Core;
import com.github.timepsilon.config.STCConfigServer;
import com.github.timepsilon.config.SqlStatsGate;
import net.minecraft.server.MinecraftServer;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Periodically flushes and saves player bank balances using wall-clock scheduling,
 * independent of server tick rate or lag.
 */
public final class BankSaveScheduler {

    // Due to critical game feature thx tim
    private static final AtomicReference<MinecraftServer> SERVER = new AtomicReference<>();
    private static volatile ScheduledExecutorService executor;

    private BankSaveScheduler() {}

    public static boolean shouldStart() {
        return SqlStatsGate.isEnabled();
    }

    public static void start(MinecraftServer server) {
        if (!shouldStart()) {
            Core.LOGGER.info("Bank save scheduler skipped (SQL stats disabled).");
            return;
        }
        stop();
        SERVER.set(server);
        int intervalSeconds = resolveIntervalSeconds(STCConfigServer.CONFIG.BANK_SAVE_INTERVAL_SECONDS.get());
        ScheduledExecutorService scheduled = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread thread = new Thread(r, "stonkstime-bank-save");
            thread.setDaemon(true);
            return thread;
        });
        executor = scheduled;
        scheduled.scheduleAtFixedRate(BankSaveScheduler::tick, intervalSeconds, intervalSeconds, TimeUnit.SECONDS);
        Core.LOGGER.info("Bank save scheduler started (every {}s)", intervalSeconds);
    }

    public static void stop() {
        SERVER.set(null);
        ScheduledExecutorService active = executor;
        executor = null;
        if (active == null) {
            return;
        }
        active.shutdown();
        try {
            if (!active.awaitTermination(5, TimeUnit.SECONDS)) {
                active.shutdownNow();
            }
        } catch (InterruptedException e) {
            active.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    private static void tick() {
        MinecraftServer server = SERVER.get();
        if (server == null) {
            return;
        }
        server.execute(BankSaveScheduler::runBankSave);
    }

    static void runBankSave() {
        MoneyDatabase.getDatabase().flushPending();
        MoneyDatabase.getDatabase().saveBanks();
    }

    static int resolveIntervalSeconds(int configuredSeconds) {
        return Math.max(1, configuredSeconds);
    }
}

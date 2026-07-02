package com.github.timepsilon.database;

import com.github.timepsilon.Core;
import com.github.timepsilon.config.STCConfigServer;
import com.github.timepsilon.config.SqlStatsGate;
import net.minecraft.server.MinecraftServer;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Periodically writes a non-WAL snapshot of the analytics database for Grafana to read.
 * <p>
 * The live database uses WAL mode, whose shared-memory file cannot be mmap'd over Docker
 * bind mounts (Grafana then fails with SQLITE_IOERR_SHMMAP / SQLITE_BUSY). Exporting a plain
 * rollback-journal copy from the mod's native JVM lets Grafana read it anywhere. Runs on a
 * dedicated thread doing only file I/O — no server-tick interaction.
 */
public final class GrafanaSnapshotScheduler {

    private static final AtomicReference<MinecraftServer> SERVER = new AtomicReference<>();
    private static volatile ScheduledExecutorService executor;

    private GrafanaSnapshotScheduler() {}

    public static void start(MinecraftServer server) {
        if (!SqlStatsGate.isEnabled()) return;
        int intervalSeconds = STCConfigServer.CONFIG.GRAFANA_SNAPSHOT_INTERVAL_SECONDS.get();
        if (intervalSeconds <= 0) {
            Core.LOGGER.info("Grafana snapshot disabled (grafanaSnapshotIntervalSeconds=0).");
            return;
        }
        stop();
        SERVER.set(server);
        ScheduledExecutorService scheduled = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread thread = new Thread(r, "stonkstime-grafana-snapshot");
            thread.setDaemon(true);
            return thread;
        });
        executor = scheduled;
        scheduled.scheduleAtFixedRate(GrafanaSnapshotScheduler::tick, intervalSeconds, intervalSeconds, TimeUnit.SECONDS);
        Core.LOGGER.info("Grafana snapshot scheduler started (every {}s)", intervalSeconds);
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
        try {
            Path live = SqliteHelper.databaseFile(server);
            if (!Files.exists(live)) {
                return;
            }
            SqliteHelper.writeGrafanaSnapshot(live, SqliteHelper.grafanaSnapshotFile(server));
        } catch (Exception e) {
            // Transient (e.g. the snapshot file briefly held open by Grafana) — retried next tick.
            Core.LOGGER.debug("Grafana snapshot failed, will retry next tick: {}", e.toString());
        }
    }
}

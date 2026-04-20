package com.github.timepsilon.utils;

import com.github.timepsilon.Core;
import net.minecraft.server.MinecraftServer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.util.ArrayList;
import java.util.List;

@EventBusSubscriber(modid = Core.MODID)
public class Scheduler {
    private static final List<ScheduledTask> CURRENT_TASKS = new ArrayList<>();

    public static void schedule(ScheduledTask task) {
        CURRENT_TASKS.add(task);
    }

    public static void tick(MinecraftServer server) {
        CURRENT_TASKS.removeIf(task -> !task.tick(server));
    }

    public interface ScheduledTask {
        boolean tick(MinecraftServer server);
    }

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        Scheduler.tick(event.getServer());
    }

    public static void runLater(int delay, Runnable runnable) {
        Scheduler.schedule(new ScheduledTask() {
            int t = delay;

            @Override
            public boolean tick(MinecraftServer server) {
                if (--t <= 0) {
                    runnable.run();
                    return false;
                }
                return true;
            }
        });
    }
}

package com.github.timepsilon.utils;

import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

public class TaskScheduler {

    /**
     * a method to delay {@code run} by delayTicks.
     * <br><b>do avoid this method if possible. use a level or a current running event you created instead</b>
     * @author Kapitencraft
     * @param delayTicks time (in ticks) to delay
     * @param run runnable to execute at the end of the delay
     */
    public static void schedule(int delayTicks, Runnable run) {
        new Object() {
            private int ticks = 0;
            private float waitTicks;

            public void start(int waitTicks) {
                this.waitTicks = waitTicks;
                NeoForge.EVENT_BUS.register(this);
            }

            @SubscribeEvent
            public void tick(ServerTickEvent.Post event) {
                this.ticks += 1;
                if (this.ticks >= this.waitTicks)
                    end();
            }
            private void end() {
                NeoForge.EVENT_BUS.unregister(this);
                run.run();
            }
        }.start(delayTicks);
    }
}

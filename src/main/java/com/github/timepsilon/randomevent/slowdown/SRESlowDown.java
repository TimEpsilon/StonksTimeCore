package com.github.timepsilon.randomevent.slowdown;

import com.github.timepsilon.randomevent.AbstractRandomStonksEvent;
import com.github.timepsilon.utils.TaskScheduler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerTickRateManager;
import net.minecraft.world.entity.player.Player;

public class SRESlowDown extends AbstractRandomStonksEvent {

    public SRESlowDown(float weight, boolean isPositive, String combination) {
        super(weight, isPositive, combination);
    }

    private static final float FACTOR = 5/20f;
    private static final int DURATION = 15*60*20; // in ticks


    @Override
    public void start(Player player) {
        MinecraftServer server = player.getServer();
        if (server == null) return;

        ServerTickRateManager tickManager = server.tickRateManager();
        tickManager.setTickRate(20*FACTOR);

        TaskScheduler.schedule((int) (DURATION*FACTOR), () -> this.stop(player));
    }

    @Override
    public void stop(Player player) {
        MinecraftServer server = player.getServer();
        if (server == null) return;

        ServerTickRateManager tickManager = server.tickRateManager();
        tickManager.setTickRate(20);
    }
}

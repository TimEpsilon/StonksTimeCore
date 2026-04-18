package com.github.timepsilon.randomevent.speedup;

import com.github.timepsilon.randomevent.AbstractRandomStonksEvent;
import com.github.timepsilon.utils.TaskScheduler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerTickRateManager;
import net.minecraft.world.entity.player.Player;

public class SRESpeedUp extends AbstractRandomStonksEvent {

    private static final float FACTOR = 4;
    private static final int DURATION = 15*60*20; // in ticks

    public SRESpeedUp(float weight, boolean isPositive, String combination) {
        super(weight, isPositive, combination);
    }

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

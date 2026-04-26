package com.github.timepsilon.stonksevent.slowdown;

import com.github.timepsilon.stonksevent.AbstractRandomStonksEvent;
import com.github.timepsilon.stonksevent.StonksEventManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerTickRateManager;
import net.minecraft.world.entity.player.Player;

public class SRESlowDown extends AbstractRandomStonksEvent {

    // TODO : desaturated shader
    public SRESlowDown(float weight, boolean isPositive, String combination, String name) {
        super(weight, isPositive, combination, name);
    }

    private static final float FACTOR = 5/20f;
    private static final int DURATION = 1*60; // in seconds


    @Override
    public void onStart(Player player) {
        MinecraftServer server = player.getServer();
        if (server == null) return;

        ServerTickRateManager tickManager = server.tickRateManager();
        tickManager.setTickRate(tickManager.tickrate()*FACTOR);

        StonksEventManager.addEvent(this, DURATION);
    }

    @Override
    public void onStop(Player player) {
        MinecraftServer server = player.getServer();
        if (server == null) return;

        ServerTickRateManager tickManager = server.tickRateManager();
        tickManager.setTickRate(tickManager.tickrate()/FACTOR);

        StonksEventManager.removeEvent(this);
    }
}

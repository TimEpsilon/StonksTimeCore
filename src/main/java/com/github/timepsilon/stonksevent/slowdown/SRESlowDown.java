package com.github.timepsilon.stonksevent.slowdown;

import com.github.timepsilon.config.STCConfigServer;
import com.github.timepsilon.packets.server.SlowDownSREPacket;
import com.github.timepsilon.stonksevent.AbstractRandomStonksEvent;
import com.github.timepsilon.stonksevent.StonksEventManager;
import com.github.timepsilon.stonksevent.TickableSRE;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerTickRateManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

public class SRESlowDown extends AbstractRandomStonksEvent implements TickableSRE {

    public SRESlowDown(float weight, boolean isPositive, String combination, String name) {
        super(weight, isPositive, combination, name);
    }

    private static final float FACTOR = (float) STCConfigServer.CONFIG.SRE_SLOW_DOWN_FACTOR.getAsDouble();
    private static final int DURATION = STCConfigServer.CONFIG.SRE_SLOW_DOWN_DURATION.get(); // in seconds

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
        MinecraftServer server = (player == null) ? ServerLifecycleHooks.getCurrentServer() : player.getServer();
        if (server == null) return;

        ServerTickRateManager tickManager = server.tickRateManager();
        tickManager.setTickRate(tickManager.tickrate()/FACTOR);

        StonksEventManager.removeEvent(this);

        for (ServerPlayer p : server.getPlayerList().getPlayers()) {
            CatnipServices.NETWORK.sendToClient(p, new SlowDownSREPacket(false));
        }
    }

    @Override
    public void tick() {
        for (ServerPlayer p : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
            CatnipServices.NETWORK.sendToClient(p, new SlowDownSREPacket(true));
        }
    }

}

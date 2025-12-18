package com.github.timepsilon.events;

import dev.ithundxr.createnumismatics.content.backend.BankAccount;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.Event;
import net.neoforged.neoforge.event.server.ServerLifecycleEvent;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

public class BankAccountUpdateEvent extends Event {

    private final BankAccount bank;
    private final int newBalance;
    private final MinecraftServer server;
    private boolean cancelled = false;


    public BankAccountUpdateEvent(BankAccount bank, int newBalance) {
        this.bank = bank;
        this.newBalance = newBalance;
        this.server = ServerLifecycleHooks.getCurrentServer();
    }

    public BankAccount getBank() {
        return bank;
    }

    public int getNewBalance() {
        return newBalance;
    }

    public MinecraftServer getServer() {
        return server;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void cancel() {
        this.cancelled = true;
    }

}

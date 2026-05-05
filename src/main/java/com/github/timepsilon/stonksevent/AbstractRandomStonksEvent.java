package com.github.timepsilon.stonksevent;

import com.github.timepsilon.Core;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractRandomStonksEvent {

    private final float weight;
    private final List<Symbol> combination;
    private final boolean isPositive;
    private final String name;

    public AbstractRandomStonksEvent(float weight, boolean isPositive, String combination, String name) {
        this(weight, isPositive, Symbol.combinationFromString(combination), name);
    }

    public AbstractRandomStonksEvent(float weight, boolean isPositive, List<Symbol> combination, String name) {
        this.weight = weight;
        this.combination = combination;
        this.isPositive = isPositive;
        this.name = name;
    }

    public final void start(Player player) {
        commonStart(player);
        onStart(player);
    }

    public final void stop(Player player) {
        commonStop(player);
        onStop(player);
    }

    public abstract void onStart(Player player);

    public abstract void onStop(Player player);

    private void commonStart(Player player) {
        MinecraftServer server = player.getServer();
        if (server == null) return;

        for (ServerPlayer p : server.getPlayerList().getPlayers()) {
            p.sendSystemMessage(
                    Component.translatable("sre.stonkstimecore.generic.starting",
                                    player.getName(),
                                    Component.translatable(String.format("sre.stonkstimecore.%s.name",name)))
                            .withColor(ChatFormatting.GOLD.getColor())
            );
            p.sendSystemMessage(
                    Component.translatable("sre.stonkstimecore.%s.description".formatted(name))
                            .withColor(ChatFormatting.GRAY.getColor())
            );
        }
        Core.LOGGER.info("{} started event {}", player.getName(), name);
    }

    private void commonStop(Player player) {
        MinecraftServer server = player.getServer();
        if (server == null) return;

        for (ServerPlayer p : server.getPlayerList().getPlayers()) {
            p.sendSystemMessage(
                    Component.translatable("sre.stonkstimecore.generic.stopping",
                                    Component.translatable(String.format("sre.stonkstimecore.%s.name",name)))
                            .withColor(ChatFormatting.GOLD.getColor())
            );
        }
    }

    public float getWeight() {
        return weight;
    }

    public String getName() {
        return name;
    }

    public List<Symbol> getCombination() {
        return combination;
    }

    public String getCombinationString() {
        StringBuilder sb = new StringBuilder();
        for (Symbol s : combination) {
            sb.append(s.toString());
        }
        return sb.toString();
    }

    public boolean isPositive() {
        return isPositive;
    }

    public static enum Symbol {
        COIN("M", -45),
        ZERO("0", 135),
        SEVEN("7", 45),
        CHERRY("C", -135);

        private final String string;
        private final float angle;
        Symbol(String string, float angle) {
            this.string = string;
            this.angle = angle;
        }

        @Override
        public String toString() {
            return this.string;
        }

        public float getAngle() {return this.angle;}

        public static Symbol fromString(String string) {
            for (Symbol symbol : Symbol.values()) {
                if (symbol.string.equals(string)) {
                    return symbol;
                }
            }
            return null;
        }

        public static List<Symbol> combinationFromString(String string) {
            List<Symbol> symbols = new ArrayList<>();
            for (char s : string.toCharArray()) {
                symbols.add(Symbol.fromString(String.valueOf(s)));
            }
            return symbols;
        }
    }
}


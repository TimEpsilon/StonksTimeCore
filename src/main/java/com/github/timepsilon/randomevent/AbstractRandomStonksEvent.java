package com.github.timepsilon.randomevent;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class AbstractRandomStonksEvent {

    private final float weight;
    private final List<Symbol> combination;
    private final boolean isPositive;
    private Component description;

    public AbstractRandomStonksEvent(float weight, boolean isPositive, Symbol symbol1, Symbol symbol2, Symbol symbol3, String description) {
        this(weight, isPositive, Arrays.asList(symbol1, symbol2, symbol3), description);
    }

    public AbstractRandomStonksEvent(float weight, boolean isPositive, String combination) {
        this(weight, isPositive, Symbol.combinationFromString(combination), null);
    }

    public AbstractRandomStonksEvent(float weight, boolean isPositive, String combination, String description) {
        this(weight, isPositive, Symbol.combinationFromString(combination), description);
    }

    public AbstractRandomStonksEvent(float weight, boolean isPositive, List<Symbol> combination, String description) {
        this.weight = weight;
        this.combination = combination;
        this.isPositive = isPositive;
        this.description = Component.translatable(description);
    }

    public abstract void start(Player player);

    public abstract void stop(Player player);

    public float getWeight() {
        return weight;
    }

    public void setDescription(String description) {
        this.description = Component.translatable("rse.stonkstimecore."+description);
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

    public Component getDescription() {
        return description;
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


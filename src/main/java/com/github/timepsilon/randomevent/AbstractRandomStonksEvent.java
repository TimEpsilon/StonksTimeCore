package com.github.timepsilon.randomevent;

import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class AbstractRandomStonksEvent {

    private final float weight;
    private final List<Symbol> combination;
    private final boolean isPositive;
    private final Component description;

    public AbstractRandomStonksEvent(float weight, boolean isPositive, Symbol symbol1, Symbol symbol2, Symbol symbol3, String description) {
        this(weight, isPositive, Arrays.asList(symbol1, symbol2, symbol3), description);
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

    public abstract void start();

    public float getWeight() {
        return weight;
    }

    public List<Symbol> getCombination() {
        return combination;
    }

    public boolean isPositive() {
        return isPositive;
    }

    public Component getDescription() {
        return description;
    }


    public static enum Symbol {
        COIN("M"),
        ZERO("0"),
        SEVEN("7"),
        APPLE("A");

        private final String string;
        Symbol(String string) {
            this.string = string;
        }

        @Override
        public String toString() {
            return this.string;
        }

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


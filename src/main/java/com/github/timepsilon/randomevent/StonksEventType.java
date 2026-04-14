package com.github.timepsilon.randomevent;

import com.github.timepsilon.randomevent.slowdown.SRESlowDown;
import com.github.timepsilon.randomevent.speedup.SRESpeedUp;
import net.minecraft.world.entity.player.Player;

import java.util.concurrent.ThreadLocalRandom;

public enum StonksEventType {

    SLOW_DOWN(5, false, "0MA", SRESlowDown::new),
    SPEED_UP(5, false, "0M7", SRESpeedUp::new);

    private final float weight;
    private final boolean isPositive;
    private final String combination;
    private final String description;
    private final EventFactory eventFactory;

    StonksEventType(float weight, boolean isPositive, String combination, EventFactory eventFactory) {
        this.weight = weight;
        this.isPositive = isPositive;
        this.combination = combination;
        this.description = name().toLowerCase();
        this.eventFactory = eventFactory;
    }

    public static void rollAndExecute(Player player) {
        float totalWeight = 0;

        for (StonksEventType eventType : StonksEventType.values()) {
            totalWeight += eventType.getWeight();
        }

        float sample = ThreadLocalRandom.current().nextFloat() * totalWeight;
        float x = 0;

        for  (StonksEventType eventType : StonksEventType.values()) {
            x += eventType.getWeight();

            if (x <= sample) {
                eventType.create().start();
                return;
            }
        }
    }

    public float getWeight() {
        return weight;
    }

    public AbstractRandomStonksEvent create() {
        return eventFactory.create(weight, isPositive, combination, description);
    }

    @FunctionalInterface
    interface EventFactory {
        AbstractRandomStonksEvent create(float weight, boolean isPositive, String combination, String description);
    }

}

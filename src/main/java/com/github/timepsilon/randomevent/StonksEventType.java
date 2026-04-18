package com.github.timepsilon.randomevent;

import com.github.timepsilon.randomevent.slowdown.SRESlowDown;
import com.github.timepsilon.randomevent.speedup.SRESpeedUp;
import net.minecraft.world.entity.player.Player;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public enum StonksEventType {

    SLOW_DOWN(new SRESlowDown(5, false, "0MC")),
    SPEED_UP(new SRESpeedUp(5, false, "0M7")),

    ;

    private final AbstractRandomStonksEvent instance;

    StonksEventType(AbstractRandomStonksEvent instance) {
        this.instance = instance;
        this.instance.setDescription(name().toLowerCase());
    }

    public static StonksEventType startRandomEvent(Player player) {
        float totalWeight = 0;

        for (StonksEventType eventType : StonksEventType.values()) {
            totalWeight += eventType.getWeight();
        }

        float sample = ThreadLocalRandom.current().nextFloat() * totalWeight;
        float x = 0;

        for  (StonksEventType eventType : StonksEventType.values()) {
            x += eventType.getWeight();

            if (x >= sample) {
                //eventType.instance.start(player);
                return eventType;
            }
        }
        return null;
    }

    public float getWeight() {
        return this.instance.getWeight();
    }

    public List<AbstractRandomStonksEvent.Symbol> getCombination() {
        return this.instance.getCombination();
    }

}

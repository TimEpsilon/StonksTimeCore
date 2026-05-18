package com.github.timepsilon.stonksevent;

import com.github.timepsilon.Core;
import net.minecraft.server.ServerTickRateManager;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * This Class manages global events, meaning something that happens to every player / the world.
 * For individual, player related events, we go through effects
 */
@EventBusSubscriber(modid = Core.MODID)
public class StonksEventManager {

    private static final HashMap<AbstractRandomStonksEvent,Float> currentEventsTimer = new HashMap<>();

    @SubscribeEvent
    public static void onTick(ServerTickEvent.Post event) {
        if (currentEventsTimer.isEmpty()) return;

        ServerTickRateManager tickManager = event.getServer().tickRateManager();
        float toSubtract = tickManager.millisecondsPerTick()/1000f;


        Iterator<Map.Entry<AbstractRandomStonksEvent, Float>> mapIterator = currentEventsTimer.entrySet().iterator();

        while (mapIterator.hasNext()) {
            Map.Entry<AbstractRandomStonksEvent, Float> entry = mapIterator.next();
            entry.setValue(entry.getValue() - toSubtract);

            if (entry.getValue() < 0) {
                // This directly removes the event from the list and the list from the map when necessary
                entry.getKey().stop(null);
            }
        }
    }

    public static void addEvent(AbstractRandomStonksEvent event, float duration) {
        // We assume the event has been started beforehand
        // duration in seconds

        currentEventsTimer.compute(event, (k,v) -> v == null ? duration : v + duration);
    }

    public static void removeEvent(AbstractRandomStonksEvent event) {
        // We assume that the event has been stopped beforehand

        Iterator<Map.Entry<AbstractRandomStonksEvent, Float>> mapIterator = currentEventsTimer.entrySet().iterator();
        while (mapIterator.hasNext()) {
            Map.Entry<AbstractRandomStonksEvent, Float> entry = mapIterator.next();

            if (entry.getKey() == event) {
                mapIterator.remove();
            }

        }
    }

    public static boolean isEventRunning(StonksEventType event) {
        return currentEventsTimer.containsKey(event.getEvent());
    }

    public static HashMap<AbstractRandomStonksEvent,Float> getCurrentEventsTimer() {
        return currentEventsTimer;
    }

}

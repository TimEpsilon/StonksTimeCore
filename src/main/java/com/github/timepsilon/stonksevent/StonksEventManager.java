package com.github.timepsilon.stonksevent;

import com.github.timepsilon.Core;
import net.minecraft.server.ServerTickRateManager;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

@EventBusSubscriber(modid = Core.MODID)
public class StonksEventManager {

    private static final HashMap<Player, List<Pair<AbstractRandomStonksEvent,Float>>> currentEventsTimer = new HashMap<>();

    @SubscribeEvent
    public static void onTick(ServerTickEvent.Post event) {
        if (currentEventsTimer.isEmpty()) return;

        ServerTickRateManager tickManager = event.getServer().tickRateManager();
        float toSubtract = tickManager.millisecondsPerTick()/1000f;

        Iterator<Map.Entry<Player, List<Pair<AbstractRandomStonksEvent, Float>>>> mapIterator = currentEventsTimer.entrySet().iterator();

        while (mapIterator.hasNext()) {
            Map.Entry<Player, List<Pair<AbstractRandomStonksEvent, Float>>> entry = mapIterator.next();
            Player player = entry.getKey();

            Iterator<Pair<AbstractRandomStonksEvent, Float>> listIterator = entry.getValue().iterator();
            while (listIterator.hasNext()) {
                Pair<AbstractRandomStonksEvent, Float> pair = listIterator.next();
                pair.setValue(pair.getValue() - toSubtract);

                if (pair.getRight() < 0) {
                    // This directly removes the event from the list and the list from the map when necessary
                    pair.getLeft().stop(player);
                }
            }
        }
    }

    public static void addEvent(Player player, AbstractRandomStonksEvent event, float duration) {
        // We assume the event has been started beforehand
        // duration in seconds

        currentEventsTimer
                .computeIfAbsent(player, k -> new ArrayList<>())
                .add(MutablePair.of(event, duration));
    }

    public static void removeEvent(Player player, AbstractRandomStonksEvent event) {
        // Since we don't really have a way to pinpoint one of two identical events
        // We will delete every one encountered
        // We assume that the event has been stopped beforehand

        Iterator<Map.Entry<Player, List<Pair<AbstractRandomStonksEvent, Float>>>> mapIterator = currentEventsTimer.entrySet().iterator();
        while (mapIterator.hasNext()) {
            Map.Entry<Player, List<Pair<AbstractRandomStonksEvent, Float>>> entry = mapIterator.next();
            Player p = entry.getKey();

            if (!p.equals(player)) continue;

            List<Pair<AbstractRandomStonksEvent, Float>> events = entry.getValue();

            entry.getValue().removeIf(pair -> pair.getLeft() == event);

            if (events.isEmpty()) {
                mapIterator.remove();
            }
        }
    }

    public static boolean isEventRunning(StonksEventType event) {
        for (Map.Entry<Player, List<Pair<AbstractRandomStonksEvent, Float>>> playerListEntry : currentEventsTimer.entrySet()) {
            for (Pair<AbstractRandomStonksEvent, Float> pair : playerListEntry.getValue()) {
                if (pair.getLeft().equals(event.getEvent())) {
                    return true;
                }
            }
        }
        return false;
    }

    public static float getEventTimer(StonksEventType event) {
        float t = 0;
        for (Map.Entry<Player, List<Pair<AbstractRandomStonksEvent, Float>>> playerListEntry : currentEventsTimer.entrySet()) {
            for (Pair<AbstractRandomStonksEvent, Float> pair : playerListEntry.getValue()) {
                if (pair.getLeft() == event.getEvent()) {
                    t += pair.getRight();
                }
            }
        }
        return t;
    }

    public static HashMap<Player, List<Pair<AbstractRandomStonksEvent,Float>>> getCurrentEventsTimer() {
        return currentEventsTimer;
    }

}

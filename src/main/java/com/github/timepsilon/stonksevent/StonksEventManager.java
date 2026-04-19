package com.github.timepsilon.stonksevent;

import com.github.timepsilon.Core;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

@EventBusSubscriber(modid = Core.MODID)
public class StonksEventManager {

    private static final HashMap<Player, List<Pair<AbstractRandomStonksEvent,Long>>> currentEvents = new HashMap<>();

    @SubscribeEvent
    public static void onTick(ServerTickEvent.Post event) {
        if (currentEvents.isEmpty()) return;

        long currentTime = new Date().getTime()/1000;

        Iterator<Map.Entry<Player, List<Pair<AbstractRandomStonksEvent, Long>>>> mapIterator = currentEvents.entrySet().iterator();

        while (mapIterator.hasNext()) {
            Map.Entry<Player, List<Pair<AbstractRandomStonksEvent, Long>>> entry = mapIterator.next();
            Player player = entry.getKey();

            Iterator<Pair<AbstractRandomStonksEvent, Long>> listIterator = entry.getValue().iterator();

            while (listIterator.hasNext()) {
                Pair<AbstractRandomStonksEvent, Long> pair = listIterator.next();

                if (pair.getRight() < currentTime) {
                    // This directly removes the event from the list and the list from the map when necessary
                    pair.getLeft().stop(player);
                }
            }
        }
    }

    public static void addEvent(Player player, AbstractRandomStonksEvent event, long duration) {
        // We assume the event has been started beforehand

        Date now = new Date();
        long currentTime = now.getTime()/1000; // in seconds
        currentEvents
                .computeIfAbsent(player, k -> new ArrayList<>())
                .add(Pair.of(event, currentTime+duration));
    }

    public static void removeEvent(Player player, AbstractRandomStonksEvent event) {
        // Since we don't really have a way to pinpoint one of two identical events
        // We will delete every one encountered
        // We assume that the event has been stopped beforehand

        Iterator<Map.Entry<Player, List<Pair<AbstractRandomStonksEvent, Long>>>> mapIterator = currentEvents.entrySet().iterator();
        while (mapIterator.hasNext()) {
            Map.Entry<Player, List<Pair<AbstractRandomStonksEvent, Long>>> entry = mapIterator.next();
            Player p = entry.getKey();

            if (!p.equals(player)) continue;

            List<Pair<AbstractRandomStonksEvent, Long>> events = entry.getValue();

            entry.getValue().removeIf(pair -> pair.getLeft() == event);

            if (events.isEmpty()) {
                mapIterator.remove();
            }
        }
    }

    public static boolean isEventRunning(StonksEventType event) {
        for (Map.Entry<Player, List<Pair<AbstractRandomStonksEvent, Long>>> playerListEntry : currentEvents.entrySet()) {
            for (Pair<AbstractRandomStonksEvent, Long> pair : playerListEntry.getValue()) {
                if (pair.getLeft() == event.getEvent()) {
                    return true;
                }
            }
        }
        return false;
    }

    public static HashMap<Player, List<Pair<AbstractRandomStonksEvent,Long>>> getCurrentEvents() {
        return currentEvents;
    }

}

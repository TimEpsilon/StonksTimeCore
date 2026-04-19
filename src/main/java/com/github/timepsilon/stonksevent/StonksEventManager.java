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
        //if (event.getServer().overworld().getGameTime() % 20 == 0) return;

        long currentTime = new Date().getTime()/1000;

        Iterator<Map.Entry<Player, List<Pair<AbstractRandomStonksEvent, Long>>>> mapIterator = currentEvents.entrySet().iterator();

        while (mapIterator.hasNext()) {
            Map.Entry<Player, List<Pair<AbstractRandomStonksEvent, Long>>> entry = mapIterator.next();
            Player player = entry.getKey();
            List<Pair<AbstractRandomStonksEvent, Long>> events = entry.getValue();

            Iterator<Pair<AbstractRandomStonksEvent, Long>> listIterator = events.iterator();

            while (listIterator.hasNext()) {
                Pair<AbstractRandomStonksEvent, Long> pair = listIterator.next();

                if (pair.getRight() < currentTime) {
                    pair.getLeft().stop(player);
                    listIterator.remove();
                }
            }

            if (events.isEmpty()) {
                mapIterator.remove();
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
            if (p != player) continue;

            List<Pair<AbstractRandomStonksEvent, Long>> events = entry.getValue();
            Iterator<Pair<AbstractRandomStonksEvent, Long>> listIterator = events.iterator();

            while (listIterator.hasNext()) {
                Pair<AbstractRandomStonksEvent, Long> pair = listIterator.next();

                if (pair.getLeft() == event) {
                    listIterator.remove();
                }
            }

            if (events.isEmpty()) {
                mapIterator.remove();
            }
        }
    }

    public static HashMap<Player, List<Pair<AbstractRandomStonksEvent,Long>>> getCurrentEvents() {
        return currentEvents;
    }

}

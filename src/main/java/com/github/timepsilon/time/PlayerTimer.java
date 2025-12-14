package com.github.timepsilon.time;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerTimer extends SavedData {

    public final HashMap<UUID, Integer> PlayerTimerMap = new HashMap<>();
    public static final int BASE_TIME = 2 * 60 * 60; // 2h
    private static final String KEY = "timers";
    private static final String DATA_ID = "player_timers";

    public static PlayerTimer getPlayerTimer(MinecraftServer level) {

        PlayerTimer timer = level.overworld().getDataStorage().computeIfAbsent(
                new Factory<>(PlayerTimer::create, PlayerTimer::load),
                DATA_ID
        );
        return timer;
    }

    public static PlayerTimer create() {
        return new PlayerTimer();
    }

    public static PlayerTimer load(CompoundTag tag, HolderLookup.Provider provider) {
        PlayerTimer t = PlayerTimer.create();
        CompoundTag root = tag.getCompound(KEY);
        for (String s : root.getAllKeys()) {
            t.PlayerTimerMap.put(UUID.fromString(s), root.getInt(s));
        }
        return t;
    }

    public static String secondsToTime(int seconds) {
        int h = seconds / 3600;
        int m = (seconds % 3600) / 60;
        int s = seconds % 60;
        return String.format("%d:%02d:%02d", h, m, s);
    }


    public PlayerTimer() {}

    public int get(UUID uuid) {
        return PlayerTimerMap.getOrDefault(uuid, 0);
    }

    public String getAsTime(UUID uuid) {
        int seconds = get(uuid);
        return secondsToTime(seconds);
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider provider) {
        CompoundTag root = new CompoundTag();
        for (Map.Entry<UUID, Integer> entry : PlayerTimerMap.entrySet()) {
            root.putInt(entry.getKey().toString(), entry.getValue());
        }
        tag.put(KEY, root);
        return tag;
    }

    public void set(UUID uuid, int value) {
        PlayerTimerMap.put(uuid, value);
        setDirty();
    }

    public void add(UUID uuid, int value) {
        PlayerTimerMap.compute(uuid, (key, val) -> val == null ? value : val + value);
        setDirty();
    }

}

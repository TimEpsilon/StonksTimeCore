package com.github.timepsilon.time;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerTimers extends SavedData {

    public final HashMap<UUID, Integer> PlayerTimerMap = new HashMap<>();
    public final HashMap<UUID, Boolean> PlayerIsOut = new HashMap<>();
    public static final int BASE_TIME = 2 * 60 * 60; // 2h
    private static final String KEY_TIMERS = "timers";
    private static final String KEY_IS_OUT = "is_out";
    private static final String DATA_ID = "player_timers";

    public static final int TIME_TO_MONEY = 3;

    public static PlayerTimers getPlayerTimer(MinecraftServer level) {

        PlayerTimers timer = level.overworld().getDataStorage().computeIfAbsent(
                new Factory<>(PlayerTimers::create, PlayerTimers::load),
                DATA_ID
        );
        return timer;
    }

    public static PlayerTimers create() {
        return new PlayerTimers();
    }

    public static String secondsToTime(int seconds) {
        int h = seconds / 3600;
        int m = (seconds % 3600) / 60;
        int s = seconds % 60;
        return String.format("%d:%02d:%02d", h, m, s);
    }

    public static PlayerTimers load(CompoundTag tag, HolderLookup.Provider provider) {
        PlayerTimers t = PlayerTimers.create();

        CompoundTag timers = tag.getCompound(KEY_TIMERS);
        for (String s : timers.getAllKeys()) {
            t.PlayerTimerMap.put(UUID.fromString(s), timers.getInt(s));
        }

        CompoundTag out = tag.getCompound(KEY_IS_OUT);
        for (String s : out.getAllKeys()) {
            t.PlayerIsOut.put(UUID.fromString(s), out.getBoolean(s));
        }
        return t;
    }


    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider provider) {
        CompoundTag timers = new CompoundTag();
        for (Map.Entry<UUID, Integer> entry : PlayerTimerMap.entrySet()) {
            timers.putInt(entry.getKey().toString(), entry.getValue());
        }

        CompoundTag out = new CompoundTag();
        for (Map.Entry<UUID, Boolean> entry : PlayerIsOut.entrySet()) {
            out.putBoolean(entry.getKey().toString(), entry.getValue());
        }

        tag.put(KEY_TIMERS, timers);
        tag.put(KEY_IS_OUT, out);
        return tag;
    }

    public PlayerTimers() {}

    public int get(UUID uuid) {
        return PlayerTimerMap.getOrDefault(uuid, 0);
    }

    public void set(UUID uuid, int value) {
        PlayerTimerMap.put(uuid, value);
        setDirty();
    }

    public void add(UUID uuid, int value) {
        PlayerTimerMap.compute(uuid, (key, val) -> val == null ? value : val + value);
        setDirty();
    }

    public void setOut(UUID uuid, boolean value) {
        PlayerIsOut.put(uuid, value);
        setDirty();
    }

    public boolean isOut(UUID uuid) {
        return PlayerIsOut.getOrDefault(uuid, false);
    }

}

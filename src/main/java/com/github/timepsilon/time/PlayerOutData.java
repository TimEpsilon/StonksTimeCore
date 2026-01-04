package com.github.timepsilon.time;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerOutData extends SavedData {

    private final HashMap<UUID, Boolean> PlayerIsOut = new HashMap<>();
    private static final String KEY_IS_OUT = "is_out";
    private static final String DATA_ID = "player_out";

    public static PlayerOutData getPlayerOutData(MinecraftServer level) {

        return level.overworld().getDataStorage().computeIfAbsent(
                new Factory<>(PlayerOutData::create, PlayerOutData::load),
                DATA_ID
        );
    }

    public static PlayerOutData create() {
        return new PlayerOutData();
    }

    public static PlayerOutData load(CompoundTag tag, HolderLookup.Provider provider) {
        PlayerOutData t = PlayerOutData.create();

        CompoundTag out = tag.getCompound(KEY_IS_OUT);
        for (String s : out.getAllKeys()) {
            t.PlayerIsOut.put(UUID.fromString(s), out.getBoolean(s));
        }
        return t;
    }


    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider provider) {
        CompoundTag out = new CompoundTag();
        for (Map.Entry<UUID, Boolean> entry : PlayerIsOut.entrySet()) {
            out.putBoolean(entry.getKey().toString(), entry.getValue());
        }

        tag.put(KEY_IS_OUT, out);
        return tag;
    }

    public PlayerOutData() {}

    public void setOut(UUID uuid, boolean value) {
        PlayerIsOut.put(uuid, value);
        setDirty();
    }

    public HashMap<UUID, Boolean> getPlayerIsOut() {
        return PlayerIsOut;
    }

    public boolean isOut(UUID uuid) {
        return PlayerIsOut.getOrDefault(uuid, false);
    }

}

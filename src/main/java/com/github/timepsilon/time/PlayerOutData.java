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

    public static PlayerOutData getPlayerTimer(MinecraftServer level) {

        PlayerOutData timer = level.overworld().getDataStorage().computeIfAbsent(
                new Factory<>(PlayerOutData::create, PlayerOutData::load),
                DATA_ID
        );
        return timer;
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

    // makes player transparent
    //@OnlyIn(Dist.CLIENT)
    //@SubscribeEvent
    //public void preRender2(RenderPlayerEvent.Pre event) {
    //    // see yourself because invisible is annoying
    //    if(Minecraft.getInstance().player.getUUID() == event.getEntity().getUUID()) {
    //        RenderSystem.enableBlend();
    //        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA,
    //                GlStateManager.DestFactor.ONE_MINUS_CONSTANT_ALPHA);
    //        // is this only triggered for items that are "active"?
    //        // would it be possible to make layers transparent?
    //        RenderSystem.setShaderColor(1.0F,1.0F, 1.0F, 0.5F);
    //    } else {
    //        // hides for other players
    //        event.setCanceled(true);
    //    }
    //}
//
    //@OnlyIn(Dist.CLIENT)
    //@SubscribeEvent
    //public void postRender2(RenderPlayerEvent.Post event) {
    //    RenderSystem.disableBlend();
    //    RenderSystem.defaultBlendFunc();
    //    RenderSystem.setShaderColor(1.0F,1.0F, 1.0F, 1.0F);
    //}

}

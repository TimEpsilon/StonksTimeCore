package com.github.timepsilon.packets.server;

import com.github.timepsilon.datamaps.SCTManager;
import com.github.timepsilon.packets.ModPackets;
import io.netty.buffer.ByteBuf;
import net.createmod.catnip.net.base.ClientboundPacketPayload;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import java.util.Map;
import java.util.HashMap;

public record SCTSyncPacket(Map<ResourceLocation, Float> values) implements ClientboundPacketPayload {

    public static final StreamCodec<ByteBuf, SCTSyncPacket> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.map(HashMap::new, ResourceLocation.STREAM_CODEC, ByteBufCodecs.FLOAT),
                    SCTSyncPacket::values,
                    SCTSyncPacket::new
            );

    @Override
    public void handle(LocalPlayer player) {
        HashMap<Item, Float> map = new HashMap<>();
        values.forEach((k, v) -> {
            BuiltInRegistries.ITEM.getOptional(k).ifPresent(item -> map.put(item, v));
        });
        SCTManager.SCT_MAP = map;
    }

    @Override
    public PacketTypeProvider getTypeProvider() {
        return ModPackets.SCT_SYNC;
    }

    // SERVER SIDE
    public static SCTSyncPacket fromCurrentMap() {
        Map<ResourceLocation, Float> out = new HashMap<>();
        SCTManager.SCT_MAP.forEach((k, v) -> {out.put(BuiltInRegistries.ITEM.getKey(k), v);});
        return new SCTSyncPacket(out);
    }
}

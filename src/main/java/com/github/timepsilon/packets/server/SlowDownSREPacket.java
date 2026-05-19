package com.github.timepsilon.packets.server;

import com.github.timepsilon.packets.ModPackets;
import com.github.timepsilon.stonksevent.slowdown.SlowDownClient;
import io.netty.buffer.ByteBuf;
import net.createmod.catnip.net.base.ClientboundPacketPayload;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record SlowDownSREPacket(boolean isRunning) implements ClientboundPacketPayload {

    public static final StreamCodec<ByteBuf, SlowDownSREPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, SlowDownSREPacket::isRunning,
            SlowDownSREPacket::new
    );

    @Override
    public PacketTypeProvider getTypeProvider() {
        return ModPackets.SLOW_DOWN_SRE;
    }

    @Override
    public void handle(LocalPlayer player) {
        SlowDownClient.IS_SLOWDOWN_RUNNING = isRunning;
    }
}

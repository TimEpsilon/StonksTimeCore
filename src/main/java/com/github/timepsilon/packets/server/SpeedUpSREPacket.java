package com.github.timepsilon.packets.server;

import com.github.timepsilon.packets.ModPackets;
import com.github.timepsilon.stonksevent.slowdown.SlowDownClient;
import com.github.timepsilon.stonksevent.speedup.SpeedUpClient;
import io.netty.buffer.ByteBuf;
import net.createmod.catnip.net.base.ClientboundPacketPayload;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record SpeedUpSREPacket(boolean isRunning) implements ClientboundPacketPayload {

    public static final StreamCodec<ByteBuf, SpeedUpSREPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, SpeedUpSREPacket::isRunning,
            SpeedUpSREPacket::new
    );

    @Override
    public PacketTypeProvider getTypeProvider() {
        return ModPackets.SPEED_UP_SRE;
    }

    @Override
    public void handle(LocalPlayer player) {
        SpeedUpClient.IS_SPEEDUP_RUNNING = isRunning;
    }
}

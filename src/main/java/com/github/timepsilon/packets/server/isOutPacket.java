package com.github.timepsilon.packets.server;

import com.github.timepsilon.packets.ModPackets;
import com.github.timepsilon.time.client.ClientOutState;
import io.netty.buffer.ByteBuf;
import net.createmod.catnip.net.base.ClientboundPacketPayload;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record isOutPacket(boolean isOut) implements ClientboundPacketPayload {

    public static final StreamCodec<ByteBuf, isOutPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, isOutPacket::isOut,
            isOutPacket::new
    );

    @Override
    public PacketTypeProvider getTypeProvider() {
        return ModPackets.IS_OUT;
    }

    @Override
    public void handle(LocalPlayer player) {
        ClientOutState.IS_OUT = isOut;
    }
}

package com.github.timepsilon.packets.server;

import com.github.timepsilon.client.gui.overlay.TimerOverlay;
import com.github.timepsilon.packets.ModPackets;
import io.netty.buffer.ByteBuf;
import net.createmod.catnip.net.base.ClientboundPacketPayload;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record TimerInfoPacket(String message) implements ClientboundPacketPayload {

    public static final StreamCodec<ByteBuf, TimerInfoPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, TimerInfoPacket::message,
            TimerInfoPacket::new
    );

    @Override
    public PacketTypeProvider getTypeProvider() {
        return ModPackets.TIMER_INFO;
    }

    @Override
    public void handle(LocalPlayer player) {
        TimerOverlay.instance.addInfo(message);
    }

}

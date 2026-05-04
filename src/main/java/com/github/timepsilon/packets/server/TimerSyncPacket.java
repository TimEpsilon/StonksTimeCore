package com.github.timepsilon.packets.server;

import com.github.timepsilon.client.gui.overlay.TimerOverlay;
import com.github.timepsilon.packets.ModPackets;
import io.netty.buffer.ByteBuf;
import net.createmod.catnip.net.base.ClientboundPacketPayload;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record TimerSyncPacket(int seconds, int money, boolean isOut) implements ClientboundPacketPayload {

    public static final StreamCodec<ByteBuf, TimerSyncPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, TimerSyncPacket::seconds,
            ByteBufCodecs.INT, TimerSyncPacket::money,
            ByteBufCodecs.BOOL,TimerSyncPacket::isOut,
            TimerSyncPacket::new
    );

    @Override
    public PacketTypeProvider getTypeProvider() {
        return ModPackets.SYNC_TIMER;
    }

    @Override
    public void handle(LocalPlayer player) {
        if (seconds >= 0) TimerOverlay.instance.setSeconds(seconds); // allows for a purely "out" packet
        if (money >= 0) TimerOverlay.instance.setMoney(money);
        TimerOverlay.instance.setOut(isOut);
    }

}

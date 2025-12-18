package com.github.timepsilon.gui.packets;

import com.github.timepsilon.gui.overlay.TimerOverlay;
import io.netty.buffer.ByteBuf;
import net.createmod.catnip.net.base.ClientboundPacketPayload;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

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
    @OnlyIn(Dist.CLIENT)
    public void handle(LocalPlayer player) {
        TimerOverlay.instance.setSeconds(seconds);
        TimerOverlay.instance.setMoney(money);
        TimerOverlay.instance.setOut(isOut);
    }
}

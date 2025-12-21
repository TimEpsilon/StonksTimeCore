package com.github.timepsilon.packets;

import com.github.timepsilon.Core;
import com.github.timepsilon.gui.overlay.TimerOverlay;
import io.netty.buffer.ByteBuf;
import net.createmod.catnip.net.base.ClientboundPacketPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
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
        if (seconds > 0) TimerOverlay.instance.setSeconds(seconds); // allows for a purely "out" packet
        if (money > 0) TimerOverlay.instance.setMoney(money);
        TimerOverlay.instance.setOut(isOut);
    }

}

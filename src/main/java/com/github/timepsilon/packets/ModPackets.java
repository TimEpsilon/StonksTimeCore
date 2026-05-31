package com.github.timepsilon.packets;

import com.github.timepsilon.Core;
import com.github.timepsilon.packets.client.StonksTemporalChronoscopeMoneyPacket;
import com.github.timepsilon.packets.server.*;
import io.netty.buffer.ByteBuf;
import net.createmod.catnip.net.base.BasePacketPayload;
import net.createmod.catnip.net.base.CatnipPacketRegistry;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.Locale;

public enum ModPackets implements BasePacketPayload.PacketTypeProvider {

    // Client -> Server
    COMPUTE_SCT(StonksTemporalChronoscopeMoneyPacket.class, StonksTemporalChronoscopeMoneyPacket.STREAM_CODEC),

    // Server -> Client
    SYNC_TIMER(TimerSyncPacket.class, TimerSyncPacket.STREAM_CODEC),
    TIMER_INFO(TimerInfoPacket.class, TimerInfoPacket.STREAM_CODEC),
    IS_OUT(IsOutPacket.class, IsOutPacket.STREAM_CODEC),
    PLAYERS_OUT_SET(PlayersAreOutPacket.class, PlayersAreOutPacket.STREAM_CODEC),
    SLOW_DOWN_SRE(SlowDownSREPacket.class, SlowDownSREPacket.STREAM_CODEC),
    SPEED_UP_SRE(SpeedUpSREPacket.class, SpeedUpSREPacket.STREAM_CODEC)
    ;


    private final CatnipPacketRegistry.PacketType<?> type;

    <T extends BasePacketPayload> ModPackets(
            Class<T> clazz,
            StreamCodec<? super ByteBuf, T> codec) {
        String name = this.name().toLowerCase(Locale.ROOT);
        this.type = new CatnipPacketRegistry.PacketType<>(
                new CustomPacketPayload.Type<>(
                        ResourceLocation.fromNamespaceAndPath(Core.MODID, name)
                ),
                clazz,
                codec
        );
    }

    @Override
    public <T extends CustomPacketPayload> CustomPacketPayload.Type<T> getType() {
        return (CustomPacketPayload.Type<T>) this.type.type();
    }

    public static void register() {
        CatnipPacketRegistry packetRegistry = new CatnipPacketRegistry(Core.MODID, "0.0.1");
        for (ModPackets packet : ModPackets.values()) {
            packetRegistry.registerPacket(packet.type);
        }
        packetRegistry.registerAllPackets();
    }
}

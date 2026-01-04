package com.github.timepsilon.packets.server;

import com.github.timepsilon.packets.ModPackets;
import com.github.timepsilon.time.PlayerOutData;
import com.github.timepsilon.time.client.ClientOutState;
import io.netty.buffer.ByteBuf;
import net.createmod.catnip.net.base.ClientboundPacketPayload;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public record PlayersAreOutPacket(Set<UUID> playersAreOutSet) implements ClientboundPacketPayload {

    public static final StreamCodec<ByteBuf, PlayersAreOutPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.collection(HashSet::new, UUIDUtil.STREAM_CODEC), PlayersAreOutPacket::playersAreOutSet,
            PlayersAreOutPacket::new
    );

    @Override
    public void handle(LocalPlayer player) {
    }

    @Override
    public PacketTypeProvider getTypeProvider() {
        return ModPackets.PLAYERS_OUT_SET;
    }

    public static Set<UUID> getOutPlayers(MinecraftServer server) {
        PlayerOutData timer = PlayerOutData.getPlayerOutData(server);
        Set<UUID> set = new HashSet<>();

        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            if (timer.isOut(player.getUUID())) {
                set.add(player.getUUID());
            }
        }

        return set;
    }
}

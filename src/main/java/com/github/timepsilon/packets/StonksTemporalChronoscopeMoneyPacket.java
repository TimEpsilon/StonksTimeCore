package com.github.timepsilon.packets;

import com.github.timepsilon.block.entity.server.StonksTemporalChronoscopeEntity;
import io.netty.buffer.ByteBuf;
import net.createmod.catnip.net.base.ServerboundPacketPayload;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public record StonksTemporalChronoscopeMoneyPacket(BlockPos STCPos) implements ServerboundPacketPayload {

    public static final StreamCodec<ByteBuf, StonksTemporalChronoscopeMoneyPacket> STREAM_CODEC = BlockPos.STREAM_CODEC.map(
            StonksTemporalChronoscopeMoneyPacket::new, StonksTemporalChronoscopeMoneyPacket::STCPos
    );



    @Override
    public PacketTypeProvider getTypeProvider() {
        return ModPackets.COMPUTE_SCT;
    }


    @Override
    public void handle(ServerPlayer player) {
        Level world = player.level();
        BlockEntity be = world.getBlockEntity(STCPos);

        if (be instanceof StonksTemporalChronoscopeEntity stcBE) {
            stcBE.computeSCTAmount();
        }
    }
}

package com.github.timepsilon.block.custom;

import com.github.timepsilon.block.entity.ModBlockEntities;
import com.github.timepsilon.block.entity.server.BankBlockEntity;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.kinetics.base.KineticBlock;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

public class BankBlock extends KineticBlock implements IBE<BankBlockEntity>, IRotate {

    public BankBlock(Properties properties) {
        super(properties);
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return Direction.Axis.Y;
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return face == Direction.DOWN;
    }

    @Override
    public Class<BankBlockEntity> getBlockEntityClass() {
        return BankBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends BankBlockEntity> getBlockEntityType() {
        return ModBlockEntities.BANK.get();
    }

    @Override
    public SpeedLevel getMinimumRequiredSpeedLevel() {
        return SpeedLevel.MEDIUM;
    }
}

package com.github.timepsilon.block.custom;

import com.github.timepsilon.block.entity.ModBlockEntities;
import com.github.timepsilon.block.entity.server.StonksTemporalChronoscopeEntity;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.kinetics.base.KineticBlock;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class StonksTemporalChronoscope extends KineticBlock implements IBE<StonksTemporalChronoscopeEntity>, IRotate {

    public static final VoxelShape SHAPE = Shapes.or(
            Block.box(0, 0, 0, 16, 8, 16),
            Block.box(1, 8, 1, 15, 14, 15)
        );


    public StonksTemporalChronoscope(Properties properties) {
        super(properties);
    }

    // Needed by IBE (Interface Block Entity)
    @Override
    public Class<StonksTemporalChronoscopeEntity> getBlockEntityClass() {
        return StonksTemporalChronoscopeEntity.class;
    }

    // Needed by IBE (Interface Block Entity)
    @Override
    public BlockEntityType<? extends StonksTemporalChronoscopeEntity> getBlockEntityType() {
        return ModBlockEntities.STONKS_TEMPORAL_CHRONOSCOPE_ENTITY.get();
    }

    @Override
    protected @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return SHAPE;
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return face == Direction.DOWN;
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return Direction.Axis.Y;
    }

    @Override
    public SpeedLevel getMinimumRequiredSpeedLevel() {
        return SpeedLevel.MEDIUM;
    }




}

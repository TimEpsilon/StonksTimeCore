package com.github.timepsilon.block.custom;

import com.github.timepsilon.block.entity.server.StonksTemporalChronoscopeEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StonksTemporalChronoscope extends BaseEntityBlock {

    public static final VoxelShape SHAPE = Shapes.or(
            Block.box(0, 0, 0, 16, 8, 16),
            Block.box(1, 8, 1, 15, 14, 15)
        );
    public static final MapCodec<StonksTemporalChronoscope> CODEC = simpleCodec(StonksTemporalChronoscope::new);


    public StonksTemporalChronoscope(Properties properties) {
        super(properties);
    }

    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos blockPos, @NotNull BlockState blockState) {
        return new StonksTemporalChronoscopeEntity(blockPos, blockState);
    }

    // Disable the vanilla renderer
    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState blockState) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    protected @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return SHAPE;
    }
}

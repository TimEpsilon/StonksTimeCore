package com.github.timepsilon.block.custom;

import com.github.timepsilon.block.entity.ModBlockEntities;
import com.github.timepsilon.block.entity.server.MoneyLeaderboardEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class MoneyLeaderboard extends Block implements EntityBlock {

    public static final int HEIGHT = 8;
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final IntegerProperty SEGMENT = IntegerProperty.create("segment", 0, HEIGHT - 1);

    private static final VoxelShape SHAPE = Block.box(2, 0, 2, 14, 16, 14);

    public MoneyLeaderboard(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState()
                .setValue(FACING, Direction.NORTH)
                .setValue(SEGMENT, 0));
    }

    public static BlockPos getMainPos(BlockState state, BlockPos pos) {
        return pos.below(state.getValue(SEGMENT));
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, SEGMENT);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos pos = context.getClickedPos();
        Level level = context.getLevel();

        if (pos.getY() + HEIGHT - 1 >= level.getMaxBuildHeight()) {
            return null;
        }

        for (int i = 0; i < HEIGHT; i++) {
            if (!level.getBlockState(pos.above(i)).canBeReplaced(context)) {
                return null;
            }
        }

        return defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection().getOpposite())
                .setValue(SEGMENT, 0);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        if (level.isClientSide) {
            return;
        }

        Direction facing = state.getValue(FACING);
        for (int segment = 1; segment < HEIGHT; segment++) {
            level.setBlock(
                    pos.above(segment),
                    defaultBlockState().setValue(FACING, facing).setValue(SEGMENT, segment),
                    Block.UPDATE_ALL
            );
        }
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock()) && !level.isClientSide) {
            BlockPos mainPos = getMainPos(state, pos);
            for (int segment = 0; segment < HEIGHT; segment++) {
                BlockPos segmentPos = mainPos.above(segment);
                if (segmentPos.equals(pos)) {
                    continue;
                }
                BlockState segmentState = level.getBlockState(segmentPos);
                if (segmentState.getBlock() == this) {
                    level.setBlock(segmentPos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
                }
            }
            if (!mainPos.equals(pos)) {
                Block.popResource(level, mainPos, new ItemStack(this));
            }
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    protected BlockState updateShape(
            BlockState state,
            Direction direction,
            BlockState neighborState,
            LevelAccessor level,
            BlockPos pos,
            BlockPos neighborPos
    ) {
        BlockPos mainPos = getMainPos(state, pos);
        int segment = state.getValue(SEGMENT);
        BlockPos expectedPos = mainPos.above(segment);

        if (!expectedPos.equals(pos)) {
            return Blocks.AIR.defaultBlockState();
        }

        BlockState mainState = level.getBlockState(mainPos);
        if (!(mainState.getBlock() instanceof MoneyLeaderboard) || mainState.getValue(SEGMENT) != 0) {
            return Blocks.AIR.defaultBlockState();
        }

        for (int i = 0; i < HEIGHT; i++) {
            BlockPos segmentPos = mainPos.above(i);
            BlockState segmentState = level.getBlockState(segmentPos);
            if (!(segmentState.getBlock() instanceof MoneyLeaderboard) || segmentState.getValue(SEGMENT) != i) {
                return Blocks.AIR.defaultBlockState();
            }
        }

        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return state.getValue(SEGMENT) == 0
                ? new MoneyLeaderboardEntity(ModBlockEntities.MONEY_LEADERBOARD_ENTITY.get(), pos, state)
                : null;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public @Nullable PushReaction getPistonPushReaction(BlockState state) {
        return PushReaction.BLOCK;
    }
}

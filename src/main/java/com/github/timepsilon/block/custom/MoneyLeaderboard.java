package com.github.timepsilon.block.custom;

import com.github.timepsilon.block.entity.ModBlockEntities;
import com.github.timepsilon.block.entity.server.MoneyLeaderboardEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MoneyLeaderboard extends Block implements EntityBlock {

    public static final int WIDTH = 8;
    public static final int HEIGHT = 8;
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final IntegerProperty PART_X = IntegerProperty.create("part_x", 0, WIDTH - 1);
    public static final IntegerProperty PART_Y = IntegerProperty.create("part_y", 0, HEIGHT - 1);

    private static final VoxelShape SHAPE = Block.box(2, 0, 2, 14, 16, 14);

    public MoneyLeaderboard(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState()
                .setValue(FACING, Direction.NORTH)
                .setValue(PART_X, 0)
                .setValue(PART_Y, 0));
    }

    public static boolean isMainPart(BlockState state) {
        return state.getValue(PART_X) == 0 && state.getValue(PART_Y) == 0;
    }

    public static BlockPos getPartOffset(Direction facing, int partX, int partY) {
        return switch (facing) {
            case NORTH -> new BlockPos(partX, partY, 0);
            case SOUTH -> new BlockPos(-partX, partY, 0);
            case EAST -> new BlockPos(0, partY, partX);
            case WEST -> new BlockPos(0, partY, -partX);
            default -> BlockPos.ZERO;
        };
    }

    public static BlockPos getPartPos(BlockPos mainPos, Direction facing, int partX, int partY) {
        return mainPos.offset(getPartOffset(facing, partX, partY));
    }

    public static BlockPos getMainPos(BlockState state, BlockPos pos) {
        return pos.subtract(getPartOffset(state.getValue(FACING), state.getValue(PART_X), state.getValue(PART_Y)));
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, PART_X, PART_Y);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos pos = context.getClickedPos();
        Level level = context.getLevel();
        Direction facing = context.getHorizontalDirection().getOpposite();

        if (pos.getY() + HEIGHT - 1 >= level.getMaxBuildHeight()) {
            return null;
        }

        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                BlockPos partPos = getPartPos(pos, facing, x, y);
                if (!level.getBlockState(partPos).canBeReplaced(context)) {
                    return null;
                }
            }
        }

        return defaultBlockState()
                .setValue(FACING, facing)
                .setValue(PART_X, 0)
                .setValue(PART_Y, 0);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        if (level.isClientSide) {
            return;
        }

        Direction facing = state.getValue(FACING);
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                if (x == 0 && y == 0) {
                    continue;
                }
                level.setBlock(
                        getPartPos(pos, facing, x, y),
                        defaultBlockState().setValue(FACING, facing).setValue(PART_X, x).setValue(PART_Y, y),
                        Block.UPDATE_ALL
                );
            }
        }
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide && !isMainPart(state)) {
            BlockPos mainPos = getMainPos(state, pos);
            BlockState mainState = level.getBlockState(mainPos);
            if (mainState.getBlock() == this && isMainPart(mainState)) {
                level.destroyBlock(mainPos, false);
            }
        }
        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock()) && !level.isClientSide) {
            BlockPos mainPos = getMainPos(state, pos);
            Direction facing = state.getValue(FACING);
            boolean isMain = mainPos.equals(pos);

            for (int x = 0; x < WIDTH; x++) {
                for (int y = 0; y < HEIGHT; y++) {
                    if (x == 0 && y == 0) {
                        continue;
                    }
                    BlockPos partPos = getPartPos(mainPos, facing, x, y);
                    if (partPos.equals(pos)) {
                        continue;
                    }
                    BlockState partState = level.getBlockState(partPos);
                    if (partState.getBlock() == this) {
                        level.removeBlock(partPos, false);
                    }
                }
            }

            if (!isMain) {
                level.removeBlock(mainPos, false);
                Block.popResource(level, pos, new ItemStack(this));
            }
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    protected List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        if (!isMainPart(state)) {
            return List.of();
        }
        return super.getDrops(state, params);
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
        Direction facing = state.getValue(FACING);
        int partX = state.getValue(PART_X);
        int partY = state.getValue(PART_Y);
        BlockPos expectedPos = getPartPos(mainPos, facing, partX, partY);

        if (!expectedPos.equals(pos)) {
            return Blocks.AIR.defaultBlockState();
        }

        BlockState mainState = level.getBlockState(mainPos);
        if (!(mainState.getBlock() instanceof MoneyLeaderboard) || !isMainPart(mainState)) {
            return Blocks.AIR.defaultBlockState();
        }

        if (!mainState.getValue(FACING).equals(facing)) {
            return Blocks.AIR.defaultBlockState();
        }

        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                BlockPos partPos = getPartPos(mainPos, facing, x, y);
                BlockState partState = level.getBlockState(partPos);
                if (!(partState.getBlock() instanceof MoneyLeaderboard)
                        || partState.getValue(PART_X) != x
                        || partState.getValue(PART_Y) != y) {
                    return Blocks.AIR.defaultBlockState();
                }
            }
        }

        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return isMainPart(state)
                ? new MoneyLeaderboardEntity(ModBlockEntities.MONEY_LEADERBOARD_ENTITY.get(), pos, state)
                : null;
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            Level level,
            BlockState state,
            BlockEntityType<T> type
    ) {
        if (level.isClientSide() || !isMainPart(state)) {
            return null;
        }
        return type == ModBlockEntities.MONEY_LEADERBOARD_ENTITY.get()
                ? (level1, pos, blockState, blockEntity) ->
                        MoneyLeaderboardEntity.serverTick(level1, pos, blockState, (MoneyLeaderboardEntity) blockEntity)
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

package com.github.timepsilon.block.custom;

import com.github.timepsilon.block.entity.ModBlockEntities;
import com.github.timepsilon.block.entity.server.SlotMachineEntity;
import com.github.timepsilon.items.ModItems;
import com.github.timepsilon.stonksevent.StonksEventType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class SlotMachine extends Block implements EntityBlock {

    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
    public static final VoxelShape SHAPE_LOW_N =  Block.box(0, 0, 0, 16, 16, 9);
    public static final VoxelShape SHAPE_LOW_E =  Block.box(6, 0, 0, 16, 16, 16);
    public static final VoxelShape SHAPE_LOW_W =  Block.box(0, 0, 0, 9, 16, 16);
    public static final VoxelShape SHAPE_LOW_S =  Block.box(0, 0, 6, 16, 16, 16);
    public static final VoxelShape SHAPE_UP_N =  Block.box(0, 0, 0, 16, 8, 9);
    public static final VoxelShape SHAPE_UP_E =  Block.box(6, 0, 0, 16, 8, 16);
    public static final VoxelShape SHAPE_UP_W =  Block.box(0, 0, 0, 9, 8, 16);
    public static final VoxelShape SHAPE_UP_S =  Block.box(0, 0, 6, 16, 8, 16);
    public static final VoxelShape SHAPE_FULL_N =  Block.box(0, 0, 0, 16, 24, 9);
    public static final VoxelShape SHAPE_FULL_E =  Block.box(6, 0, 0, 16, 24, 16);
    public static final VoxelShape SHAPE_FULL_W =  Block.box(0, 0, 0, 9, 24, 16);
    public static final VoxelShape SHAPE_FULL_S =  Block.box(0, 0, 6, 16, 24, 16);


    public SlotMachine(Properties properties) {
        super(properties);
        this.registerDefaultState(
                this.stateDefinition.any()
                        .setValue(FACING, Direction.NORTH)
                        .setValue(HALF, DoubleBlockHalf.LOWER)
        );
    }

    public static BlockPos getMainPos(BlockState state, BlockPos pos) {
        return state.getValue(HALF) == DoubleBlockHalf.LOWER ? pos : pos.below();
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (level.isClientSide) return ItemInteractionResult.SUCCESS;
        if (!(level.getBlockEntity(getMainPos(state, pos)) instanceof SlotMachineEntity be)) return ItemInteractionResult.FAIL;
        if (be.isActive()) return ItemInteractionResult.FAIL;

        if (stack.is(ModItems.GOLDEN_TICKET)) {
            StonksEventType event = StonksEventType.startRandomEvent(player,4f, be);
            if (event != null) be.interact(event);

            if (!player.isCreative()) stack.shrink(1);
            return ItemInteractionResult.CONSUME;
        }
        return ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, HALF);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos blockpos = context.getClickedPos();
        BlockGetter level =  context.getLevel();

        if (blockpos.getY() >= level.getMaxBuildHeight()-1 || !context.canPlace()) {
            return null;
        }

        return this.defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection())
                .setValue(HALF, DoubleBlockHalf.LOWER);
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide && (player.isCreative() || !player.hasCorrectToolForDrops(state, level, pos))) {
            DoubleBlockHalf half = state.getValue(HALF);
            if (half == DoubleBlockHalf.UPPER) {
                BlockPos otherPos = pos.below();
                BlockState otherState = level.getBlockState(otherPos);
                if (otherState.is(this) && otherState.getValue(HALF) == DoubleBlockHalf.LOWER) {
                    level.setBlock(otherPos, Blocks.AIR.defaultBlockState(), 35);
                    level.levelEvent(player, 2001, otherPos, Block.getId(otherState));
                }
            }
        }

        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack tool) {
        super.playerDestroy(level, player, pos, state, blockEntity, tool);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        level.setBlock(pos.above(), state.setValue(HALF, DoubleBlockHalf.UPPER), 3);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return blockState.getValue(HALF) == DoubleBlockHalf.LOWER
                ? new SlotMachineEntity(ModBlockEntities.SLOT_MACHINE_ENTITY.get(), blockPos, blockState)
                : null;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        boolean bottom = state.getValue(HALF) == DoubleBlockHalf.LOWER;
        return switch (state.getValue(FACING)) {
            case EAST -> bottom ? SHAPE_LOW_E : SHAPE_UP_E;
            case SOUTH -> bottom ? SHAPE_LOW_S : SHAPE_UP_S;
            case WEST -> bottom ? SHAPE_LOW_W : SHAPE_UP_W;
            default -> bottom ? SHAPE_LOW_N : SHAPE_UP_N;
        };
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState facingState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        DoubleBlockHalf half = state.getValue(HALF);
        BlockPos otherPos = (half == DoubleBlockHalf.LOWER) ? pos.above() : pos.below();
        BlockState otherState = level.getBlockState(otherPos);

        if (!otherState.is(this)) {
            BlockState air = Blocks.AIR.defaultBlockState();

            level.setBlock(otherPos, air, 35);
            level.levelEvent(null, 2001, pos, Block.getId(air));
            return air;
        }

        return super.updateShape(state, direction, facingState, level, pos, neighborPos);
    }

    @Override
    public @Nullable PushReaction getPistonPushReaction(BlockState state) {
        return PushReaction.IGNORE;
    }
}

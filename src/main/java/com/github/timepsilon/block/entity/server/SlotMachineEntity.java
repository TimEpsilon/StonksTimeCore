package com.github.timepsilon.block.entity.server;

import com.github.timepsilon.randomevent.AbstractRandomStonksEvent;
import com.github.timepsilon.randomevent.StonksEventType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;

public class SlotMachineEntity extends BlockEntity implements GeoBlockEntity {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final RawAnimation SPINNING = RawAnimation.begin().thenPlay("spinning");
    private final RawAnimation SCORE = RawAnimation.begin().thenPlay("score");
    private final static String ANGLE_WHEEL1 = "angle_wheel1";
    private final static String ANGLE_WHEEL2 = "angle_wheel2";
    private final static String ANGLE_WHEEL3 = "angle_wheel3";

    private float angleWheel1, angleWheel2, angleWheel3;

    public SlotMachineEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }

    public void interact(StonksEventType event) {
        stopTriggeredAnim("spinController","spinning");
        stopTriggeredAnim("scoreController","score");

        List<AbstractRandomStonksEvent.Symbol> currentScore = event.getCombination();
        angleWheel1 = currentScore.get(0).getAngle();
        angleWheel2 = currentScore.get(1).getAngle();
        angleWheel3 = currentScore.get(2).getAngle();
        setChanged();

        System.out.println(currentScore);

        triggerAnim("spinController","spinning");
        triggerAnim("scoreController","score");
    }

    public float getAngleWheel1() {return angleWheel1;}
    public float getAngleWheel2() {return angleWheel2;}
    public float getAngleWheel3() {return angleWheel3;}

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controller) {
        controller.add(
                new AnimationController<>(this, "spinController", 0, state -> PlayState.CONTINUE)
                        .triggerableAnim("spinning", SPINNING)
        );

        controller.add(
                new AnimationController<>(this, "scoreController", 0, state -> PlayState.CONTINUE)
                        .triggerableAnim("score", SCORE)
        );
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putFloat(ANGLE_WHEEL1, angleWheel1);
        tag.putFloat(ANGLE_WHEEL2, angleWheel2);
        tag.putFloat(ANGLE_WHEEL3, angleWheel3);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        angleWheel1 = (tag.contains(ANGLE_WHEEL1)) ? tag.getFloat(ANGLE_WHEEL1) : 0;
        angleWheel2 = (tag.contains(ANGLE_WHEEL2)) ? tag.getFloat(ANGLE_WHEEL2) : 0;
        angleWheel3 = (tag.contains(ANGLE_WHEEL3)) ? tag.getFloat(ANGLE_WHEEL3) : 0;

    }

    @Override
    public void setChanged() {
        super.setChanged();
        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
        }
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return this.saveWithoutMetadata(registries);
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

}

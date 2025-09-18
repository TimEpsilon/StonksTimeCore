package com.github.timepsilon.block.entity.server;

import com.github.timepsilon.block.entity.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.animation.*;

public class StonksTemporalChronoscopeEntity extends BlockEntity implements GeoBlockEntity {

    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    public StonksTemporalChronoscopeEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.STONKS_TEMPORAL_CHRONOSCOPE_ENTITY.get(), pos, state);
    }

    // Animation handling
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, this::predicate));
    }

    // Holds animatable instance, allowing it to be retrieved by the renderer
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> tAnimationState) {
        tAnimationState.getController().setAnimation(RawAnimation.begin().then("spinning", Animation.LoopType.LOOP));
        return PlayState.CONTINUE;
    }

}

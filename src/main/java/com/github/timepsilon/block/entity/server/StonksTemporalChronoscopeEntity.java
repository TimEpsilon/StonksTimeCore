package com.github.timepsilon.block.entity.server;

import com.github.timepsilon.block.entity.ModBlockEntities;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.loading.math.MathParser;

public class StonksTemporalChronoscopeEntity extends KineticBlockEntity implements GeoBlockEntity {

    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    public StonksTemporalChronoscopeEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.STONKS_TEMPORAL_CHRONOSCOPE_ENTITY.get(), pos, state);
    }

    // Animation handling
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        // We use this.speed as a factor to multiply the animation speed by
        MathParser.setVariable("query.stonkstimecore_input_speed", this::getSpeed);
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

    public float getSpeed() {
        return this.speed;
    }

}

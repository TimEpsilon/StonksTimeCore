package com.github.timepsilon.entity.custom;

import com.github.timepsilon.items.ModItems;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.PushReaction;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

public class TimeGearEntity extends Entity implements GeoEntity {
    protected static final RawAnimation TICKING = RawAnimation.begin().thenLoop("clock");
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    protected static final EntityDataAccessor<Optional<UUID>> DATA_OWNERUUID_ID;

    static {
        DATA_OWNERUUID_ID = SynchedEntityData.defineId(TimeGearEntity.class, EntityDataSerializers.OPTIONAL_UUID);
    }

    public TimeGearEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
        this.refreshDimensions();
        this.blocksBuilding = true;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_OWNERUUID_ID, Optional.empty());
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        UUID uuid = compound.getUUID("Player");
        this.setPlayerUUID(uuid);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        if (this.getPlayerUUID() != null) {
            compound.putUUID("Player", this.getPlayerUUID());
        }
    }

    @Override
    public void tick() {

    }


    @Override
    public PushReaction getPistonPushReaction() {
        return PushReaction.IGNORE;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    public ItemStack getPickResult() {
        return new ItemStack(ModItems.TIME_GEAR.get());
    }

    public void setPlayer(Player player) {
        setPlayerUUID(player.getUUID());
    }

    public void setPlayerUUID(UUID uuid) {
        this.entityData.set(DATA_OWNERUUID_ID, Optional.of(uuid)) ;
    }

    @Nullable
    public UUID getPlayerUUID() {
        return (UUID)((Optional)this.entityData.get(DATA_OWNERUUID_ID)).orElse(null);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        super.hurt(source, amount);
        if (this.isRemoved()) {
            return false;
        }

        if (this.level() instanceof ServerLevel) {

            if (source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
                this.kill();
                return false;
            }

            if (source.getDirectEntity() instanceof Player) {
                if (!source.isCreativePlayer()) {
                    this.dropItem();
                }
                this.playBrokenSound();
                this.showBreakingParticles();
                this.kill();
                return true;
            }
        }
        return false;
    }

    private void showBreakingParticles() {
        if (this.level() instanceof ServerLevel) {
            ((ServerLevel)this.level())
                    .sendParticles(
                            new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(ModItems.TIME_GEAR.get())),
                            this.getX(),
                            this.getY(),
                            this.getZ(),
                            10,
                            this.getBbWidth() / 4.0F,
                            this.getBbHeight() / 4.0F,
                            this.getBbWidth() / 4.0F,
                            0.05);
        }
    }

    private void dropItem() {
        ItemStack itemstack = new ItemStack(ModItems.TIME_GEAR.get());
        Block.popResource(this.level(), this.blockPosition(), itemstack);
    }

    private void playBrokenSound() {
        // TODO Custom sound
        this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.AMETHYST_BLOCK_BREAK, this.getSoundSource(), 1.0F, 1.0F);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, this::predicate));
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> state) {
        state.getController().setAnimation(TICKING);
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }


}

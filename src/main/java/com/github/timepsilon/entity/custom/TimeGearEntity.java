package com.github.timepsilon.entity.custom;

import com.github.timepsilon.items.ModItems;
import com.github.timepsilon.time.PlayerOutData;
import com.github.timepsilon.utils.TimeUtils;
import dev.ithundxr.createnumismatics.Numismatics;
import dev.ithundxr.createnumismatics.content.backend.BankAccount;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
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
import net.neoforged.neoforge.common.UsernameCache;
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
    private PlayerOutData outData;
    private BankAccount bank;

    // This ensures sync between client and server
    protected static final EntityDataAccessor<Optional<UUID>> DATA_OWNERUUID_ID;
    protected static final EntityDataAccessor<Byte> DATA_STATE;

    static {
        DATA_OWNERUUID_ID = SynchedEntityData.defineId(TimeGearEntity.class, EntityDataSerializers.OPTIONAL_UUID);
        DATA_STATE = SynchedEntityData.defineId(TimeGearEntity.class, EntityDataSerializers.BYTE);
    }

    public TimeGearEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
        this.refreshDimensions();
        this.blocksBuilding = true;

        this.setCustomName(Component.literal("00:00:00").withStyle(ChatFormatting.GOLD));

        if (this.getServer() != null) this.outData = PlayerOutData.getPlayerOutData(this.getServer());
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_OWNERUUID_ID, Optional.empty());
        builder.define(DATA_STATE, (byte) State.OFFLINE.ordinal());
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        UUID uuid = compound.getUUID("Player");
        this.setPlayerUUID(uuid);

        State state = State.values()[compound.getByte("State")];
        this.setState(state);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        if (this.getPlayerUUID() != null) {
            compound.putUUID("Player", this.getPlayerUUID());
        }
        compound.putByte("State", (byte) this.getState().ordinal());
    }

    @Override
    public void tick() {
        if (!this.level().isClientSide()) setState(computeState());

        if (!this.getState().shouldSpin()) return;

        if (this.bank != null) {
            int seconds = this.bank.getBalance() / TimeUtils.TIME_TO_MONEY;
            String time = TimeUtils.secondsToTime(seconds);
            this.setCustomName(Component.literal(time).withStyle(ChatFormatting.GOLD));

        }
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

    public void setState(State state) {
        this.entityData.set(DATA_STATE, (byte) state.ordinal());
    }

    public State getState() {
        return State.values()[this.entityData.get(DATA_STATE)];
    }

    public State computeState() {
        boolean isOut = this.outData.isOut(getPlayerUUID());
        if (isOut) {
            return State.OUT;
        } else {
            for (Player player : this.getServer().getPlayerList().getPlayers()) {
                if (player.getUUID().equals(this.getPlayerUUID())) return State.ONLINE;
            }
            return State.OFFLINE;
        }
    }

    public void setPlayerUUID(UUID uuid) {
        this.entityData.set(DATA_OWNERUUID_ID, Optional.of(uuid));

        // Update the bank
        this.bank = Numismatics.BANK.getOrCreateAccount(uuid, BankAccount.Type.PLAYER);
    }

    public void setPlayer(Player player) {
        setPlayerUUID(player.getUUID());
    }

    @Nullable
    public UUID getPlayerUUID() {
        return (UUID)((Optional)this.entityData.get(DATA_OWNERUUID_ID)).orElse(null);
    }

    @Nullable
    public String getOwnerName() {
        if (this.getPlayerUUID() == null) return null;
        if (UsernameCache.containsUUID(this.getPlayerUUID())) {
            return UsernameCache.getLastKnownUsername(this.getPlayerUUID());
        }
        return null;
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
        this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.AMETHYST_BLOCK_BREAK, this.getSoundSource(), 1.0F, 1.0F);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "ticking", 1, this::predicate));
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> state) {
        if (this.getState().shouldSpin) {
            state.getController().setAnimation(TICKING);
            return PlayState.CONTINUE;
        }
        state.getController().stop();
        state.getController().forceAnimationReset();
        state.resetCurrentAnimation();
        return PlayState.STOP;
    }


    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }

    public static enum State {
        ONLINE(true,false),
        OFFLINE(false,false),
        OUT(false,true);

        private final boolean shouldSpin;
        private final boolean isOut;

        State(boolean shouldSpin, boolean isOut) {
            this.shouldSpin = shouldSpin;
            this.isOut = isOut;
        }

        public boolean shouldSpin() {return shouldSpin;}

        public boolean isOut() {return isOut;}
    }


}

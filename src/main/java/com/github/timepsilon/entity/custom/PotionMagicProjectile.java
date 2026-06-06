package com.github.timepsilon.entity.custom;

import com.github.timepsilon.entity.ModEntities;
import com.github.timepsilon.particle.ModParticles;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.entity.spells.AbstractMagicProjectile;
import io.redspace.ironsspellbooks.particle.BlastwaveParticleOptions;
import net.createmod.catnip.theme.Color;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.Optional;

public class PotionMagicProjectile extends AbstractMagicProjectile {

    public PotionMagicProjectile(EntityType<? extends Projectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    protected static final EntityDataAccessor<Integer> DATA_COLOR;
    MobEffectInstance effect;

    static {
        DATA_COLOR = SynchedEntityData.defineId(PotionMagicProjectile.class, EntityDataSerializers.INT);
    }


    public PotionMagicProjectile(Level level, LivingEntity shooter, @Nullable MobEffectInstance potionEffect) {
        this(ModEntities.POTION_MAGIC_PROJECTILE.get(), level);
        this.effect = potionEffect;
        setOwner(shooter);
        setColor((effect != null) ? effect.getEffect().value().getColor() : Color.WHITE.getRGB());
    }

    @Override
    public void trailParticles() {
        Vec3 trailTime = getDeltaMovement();
        double d0 = this.getX() - trailTime.x;
        double d1 = this.getY() - trailTime.y;
        double d2 = this.getZ() - trailTime.z;
        var count = Mth.clamp((int) (trailTime.lengthSqr() * 2), 1, 6);
        for (int i = 0; i < count; i++) {
            Vec3 random = Utils.getRandomVec3(getBbHeight() * .2f);
            var f = i / ((float) count);
            var x = Mth.lerp(f, d0, this.getX() + trailTime.x);
            var y = Mth.lerp(f, d1, this.getY() + trailTime.y);
            var z = Mth.lerp(f, d2, this.getZ() + trailTime.z);
            this.level().addParticle(ModParticles.TIME_PARTICLES.get(), true,x - random.x, y + getBbHeight() * .5f - random.y, z - random.z, 0,0,0);
        }

        Vec3 potionDrip = this.position().subtract(getDeltaMovement().scale(2));
        this.level().addParticle(ParticleTypes.EFFECT, potionDrip.x, potionDrip.y, potionDrip.z, 0, 0, 0);
    }

    @Override
    public void impactParticles(double x, double y, double z) {
        MagicManager.spawnParticles(this.level(), ModParticles.TIME_PARTICLES.get(), x, y, z, 50, 3.5, 3.5, 3.5, 0.3, true);
        MagicManager.spawnParticles(this.level(), ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, FastColor.ARGB32.opaque(getColor())), x, y, z, 100, 1.7, 0.2, 1.7, 0, true);
        Color color = new Color(getColor());
        MagicManager.spawnParticles(this.level(), new BlastwaveParticleOptions(color.asVectorF(),3.5f), x, y, z, 1, 0, 0, 0, 0, true);
    }

    @Override
    protected void onHit(HitResult hitresult) {
        super.onHit(hitresult);
        if (!this.level().isClientSide) {

            if (this.effect != null) {
                float explosionRadius = 3.5f;
                var entities = level().getEntities(this, this.getBoundingBox().inflate(explosionRadius));

                for (Entity entity : entities) {
                    double distance = entity.position().distanceTo(hitresult.getLocation());
                    if (distance < explosionRadius && Utils.hasLineOfSight(level(),
                            hitresult.getLocation(),
                            entity.getEyePosition(),
                            true)) {

                        if (entity instanceof LivingEntity livingEntity /*&& livingEntity != getOwner()*/)
                            livingEntity.addEffect(effect);
                    }
                }
            }

            this.discardHelper(hitresult);
        }
    }

    public int getColor() {
        return entityData.get(DATA_COLOR);
    }

    private void setColor(int color) {
        entityData.set(DATA_COLOR, color);
    }

    @Override
    public float getSpeed() {
        return 2f;
    }

    @Override
    public Optional<Holder<SoundEvent>> getImpactSound() {
        return Optional.of(BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.FIREWORK_ROCKET_LARGE_BLAST));
    }



    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        if (effect != null) {
            tag.put("effect",effect.save());
        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("effect")) {
            CompoundTag compoundtag = tag.getCompound("effect");
            MobEffectInstance mobeffectinstance = MobEffectInstance.load(compoundtag);
            if (mobeffectinstance != null) {
                this.effect = mobeffectinstance;
            }
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_COLOR,Color.WHITE.getRGB());
    }
}

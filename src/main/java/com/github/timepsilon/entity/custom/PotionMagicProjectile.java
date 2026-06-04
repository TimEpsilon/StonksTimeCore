package com.github.timepsilon.entity.custom;

import com.github.timepsilon.entity.ModEntities;
import com.github.timepsilon.particle.ModParticles;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.entity.spells.AbstractMagicProjectile;
import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import net.createmod.catnip.theme.Color;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
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
        Vec3 vec3 = getDeltaMovement();
        double d0 = this.getX() - vec3.x;
        double d1 = this.getY() - vec3.y;
        double d2 = this.getZ() - vec3.z;
        var count = Mth.clamp((int) (vec3.lengthSqr() * 2), 1, 4);
        for (int i = 0; i < count; i++) {
            Vec3 random = Utils.getRandomVec3(getBbHeight() * .2f);
            var f = i / ((float) count);
            var x = Mth.lerp(f, d0, this.getX() + vec3.x);
            var y = Mth.lerp(f, d1, this.getY() + vec3.y);
            var z = Mth.lerp(f, d2, this.getZ() + vec3.z);
            this.level().addParticle(ModParticles.TIME_PARTICLES.get(), true,x - random.x, y + getBbHeight() * .5f - random.y, z - random.z, 0,0,0);
        }
    }

    @Override
    public void impactParticles(double x, double y, double z) {

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
        return 1f;
    }

    @Override
    public Optional<Holder<SoundEvent>> getImpactSound() {
        return Optional.empty();
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

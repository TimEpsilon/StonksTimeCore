package com.github.timepsilon.ironsspellbooks;

import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.entity.spells.AbstractMagicProjectile;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public abstract class AbstractPotionMagicProjectile extends AbstractMagicProjectile {

    public AbstractPotionMagicProjectile(EntityType<? extends Projectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.setNoGravity(true);
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
            this.level().addParticle(ParticleHelper.COMET_FOG, true,x - random.x, y + getBbHeight() * .5f - random.y, z - random.z, 0,0,0);
        }
    }

    @Override
    public float getSpeed() {
        return 2f;
    }

}

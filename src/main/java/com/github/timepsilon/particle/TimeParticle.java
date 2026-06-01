package com.github.timepsilon.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;

public class TimeParticle extends TextureSheetParticle {


    public TimeParticle(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, SpriteSet sprites) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed);

        int roll = level.random.nextInt(100);
        if  (roll < 80) {
            setSprite(sprites.get(1,2));
        } else if (roll < 90) {
            setSprite(sprites.get(0,2));
        } else {
            setSprite(sprites.get(2,2));
        }

        this.lifetime = (int) (this.random.nextDouble() * 20) + 50;
        this.quadSize = 0.1f + this.random.nextFloat() * 0.1f;
        this.hasPhysics = false;
        this.xd *= 0.1;
        this.yd *= 0.1;
        this.zd *= 0.1;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

}

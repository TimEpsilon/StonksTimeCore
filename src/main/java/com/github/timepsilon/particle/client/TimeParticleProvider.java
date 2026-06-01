package com.github.timepsilon.particle.client;

import com.github.timepsilon.particle.TimeParticle;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import org.jetbrains.annotations.Nullable;

public class TimeParticleProvider implements ParticleProvider<SimpleParticleType> {

    private final SpriteSet sprites;

    public TimeParticleProvider(SpriteSet sprites) {
        this.sprites = sprites;
    }

    @Override
    public @Nullable Particle createParticle(SimpleParticleType simpleParticleType, ClientLevel clientLevel, double x, double y, double z, double vx, double vy, double vz) {
        return new TimeParticle(clientLevel, x, y, z, vx, vy, vz, sprites);
    }
}

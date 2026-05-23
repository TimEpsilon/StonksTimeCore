package com.github.timepsilon.stonksevent.boost;

import com.github.timepsilon.stonksevent.AbstractRandomStonksEvent;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class SREBoost extends AbstractRandomStonksEvent {

    public SREBoost(float weight, boolean isPositive, String combination, String name) {
        super(weight, isPositive, combination, name);
    }

    @Override
    public void onStart(Player player) {
        Vec3 look = player.getLookAngle().reverse().normalize();
        Vec3 boost = (new Vec3(look.x, 1, look.z)).multiply(5,20,5);
        player.setDeltaMovement(boost);
        player.hurtMarked = true;

        // particles
        ((ServerLevel)player.level()).sendParticles(
                (ServerPlayer) player,
                ParticleTypes.CLOUD,
                true,
                player.getX(), player.getY(), player.getZ(),
                200,
                look.x, 1, look.z,
                0.2);
    }

    @Override
    public void onStop(@Nullable Player player) {

    }
}

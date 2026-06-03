package com.github.timepsilon.stonksevent.timeless;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class TimelessEffect extends MobEffect {

    public TimelessEffect(MobEffectCategory category, int color) {
        super(category, color);
    }


    @Override
    public boolean applyEffectTick(LivingEntity livingEntity, int amplifier) {
        return livingEntity instanceof Player p;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }
}

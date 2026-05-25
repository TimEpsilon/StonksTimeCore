package com.github.timepsilon.stonksevent.growthspurt;

import com.github.timepsilon.pehkui.ModPehkuiModifier;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import virtuoel.pehkui.api.ScaleTypes;

public class GrowthSpurtEffect extends MobEffect {

    public GrowthSpurtEffect(MobEffectCategory category, int color) {
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

    @Override
    public void onEffectStarted(LivingEntity livingEntity, int amplifier) {
        super.onEffectStarted(livingEntity, amplifier);
        ScaleTypes.BASE.getScaleData(livingEntity).getBaseValueModifiers().add(ModPehkuiModifier.GROWTH_SPURT);
    }
}

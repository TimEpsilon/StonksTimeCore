package com.github.timepsilon.stonksevent.lifelink;

import com.github.timepsilon.Core;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class LifeLinkEffect extends MobEffect {

    public LifeLinkEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    public static final ResourceKey<DamageType> LIFELINK_DAMAGE = ResourceKey.create(Registries.DAMAGE_TYPE,
            ResourceLocation.fromNamespaceAndPath(Core.MODID, "lifelink"));

    @Override
    public boolean applyEffectTick(LivingEntity livingEntity, int amplifier) {
        return livingEntity instanceof Player p;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }
}

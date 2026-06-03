package com.github.timepsilon.ironsspellbooks.spells;

import com.github.timepsilon.Core;
import com.github.timepsilon.ironsspellbooks.ModSchoolRegistry;
import com.github.timepsilon.particle.ModParticles;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.spells.*;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.entity.spells.target_area.TargetedAreaEntity;
import io.redspace.ironsspellbooks.spells.TargetAreaCastData;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class Boost extends AbstractSpell {

    private final DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.COMMON)
            .setSchoolResource(ModSchoolRegistry.TIME_RESSOURCE)
            .setMaxLevel(5)
            .setCooldownSeconds(300)
            .build();

    @Override
    public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        return List.of(
                Component.translatable("ui.irons_spellbooks.distance", Utils.stringTruncation(getRadius(spellLevel, caster), 1)),
                Component.translatable("ui.stonkstimecore.power", Utils.stringTruncation(getAmplitude(spellLevel, caster).x, 1))
        );
    }

    private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath(Core.MODID, "boost");

    public Boost() {
        this.manaCostPerLevel = 20;
        this.baseSpellPower = 5;
        this.spellPowerPerLevel = 2;
        this.castTime = 20;
        this.baseManaCost = 70;
    }

    @Override
    public boolean checkPreCastConditions(Level level, int spellLevel, LivingEntity entity, MagicData playerMagicData) {
        float radius = getRadius(spellLevel, entity);
        var area = TargetedAreaEntity.createTargetAreaEntity(level, entity.position(), radius, Utils.packRGB(this.getTargetingColor()), entity);
        playerMagicData.setAdditionalCastData(new TargetAreaCastData(entity.position(), area));
        return true;
    }

    @Override
    public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
        float radius = getRadius(spellLevel, entity);
        level.getEntitiesOfClass(LivingEntity.class, AABB.ofSize(entity.getBoundingBox().getCenter(), radius*2, radius*2, radius*2))
                .forEach(livingEntity -> {
                    if (livingEntity == entity) return;

                    Vec3 direction = livingEntity.position().subtract(entity.position()).normalize().add(0,1,0);
                    direction = direction.multiply(getAmplitude(spellLevel, entity));

                    MagicManager.spawnParticles(level, ParticleTypes.CLOUD, livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), 50,
                            livingEntity.getBbWidth() * 0.5, livingEntity.getBbWidth() * 0.5, livingEntity.getBbWidth() * 0.5,
                            0.1, false);

                    livingEntity.setDeltaMovement(direction);
                    livingEntity.hurtMarked = true;
        });

        MagicManager.spawnParticles(level, ModParticles.TIME_PARTICLES.get(), entity.getX(), entity.getY(), entity.getZ(), 50,
                radius/2, 1, radius/2,
                0, true);

        super.onCast(level, spellLevel, entity, castSource, playerMagicData);
    }

    @Override
    public ResourceLocation getSpellResource() {
        return spellId;
    }

    @Override
    public DefaultConfig getDefaultConfig() {
        return defaultConfig;
    }

    @Override
    public CastType getCastType() {
        return CastType.LONG;
    }

    @Override
    public AnimationHolder getCastFinishAnimation() {
        return SpellAnimations.TOUCH_GROUND_ANIMATION;
    }

    private float getRadius(int spellLevel, LivingEntity entity) {
        return getSpellPower(spellLevel, entity)/2 + 0.5f;
    }

    private Vec3 getAmplitude(int spellLevel, LivingEntity entity) {
        float power = getSpellPower(spellLevel, entity)*1.5f;
        return new Vec3(power/baseSpellPower, power/baseSpellPower, power/baseSpellPower);
    }
}

package com.github.timepsilon.ironsspellbooks.spells;

import com.github.timepsilon.Core;
import com.github.timepsilon.entity.custom.PotionMagicProjectile;
import com.github.timepsilon.ironsspellbooks.ModSchoolRegistry;
import com.github.timepsilon.mobeffect.ModMobEffects;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.spells.*;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class Mirror extends AbstractSpell {

    private final DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.COMMON)
            .setSchoolResource(ModSchoolRegistry.TIME_RESSOURCE)
            .setMaxLevel(1)
            .setCooldownSeconds(180)
            .build();

    @Override
    public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        return List.of(

        );
    }

    private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath(Core.MODID, "mirror");

    public Mirror() {
        this.manaCostPerLevel = 0;
        this.baseSpellPower = 1;
        this.spellPowerPerLevel = 0;
        this.castTime = 20*2;
        this.baseManaCost = 70;
    }

    @Override
    public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
        PotionMagicProjectile orb = new PotionMagicProjectile(level, entity,
                new MobEffectInstance(ModMobEffects.MIRROR, 100, 0, false, true));
        orb.setPos(entity.position().add(0, entity.getEyeHeight() - orb.getBoundingBox().getYsize() * .5f, 0).add(entity.getForward()));
        orb.shoot(entity.getLookAngle());
        orb.setDeltaMovement(orb.getDeltaMovement().add(0, 0.2, 0));
        orb.setExplosionRadius(getRadius(spellLevel, entity));
        level.addFreshEntity(orb);
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

package com.github.timepsilon.ironsspellbooks.spells;

import com.github.timepsilon.entity.custom.PotionMagicProjectile;
import com.github.timepsilon.mobeffect.ModMobEffects;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellAnimations;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import java.util.Optional;

public abstract class AbstractPotionSpell extends AbstractSpell {

    public Holder<MobEffect> effectHolder;
    public int duration;

    public AbstractPotionSpell(Holder<MobEffect> effectHolder, int duration) {
        this.effectHolder = effectHolder;
        this.duration = duration;
        this.spellPowerPerLevel = 1;
        this.baseSpellPower = 0;
    }

    @Override
    public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
        PotionMagicProjectile orb = new PotionMagicProjectile(level, entity,
                new MobEffectInstance(effectHolder, duration*20, (int) getSpellPower(spellLevel, entity), false, true));
        orb.setPos(entity.position().add(0, entity.getEyeHeight() - orb.getBoundingBox().getYsize() * .5f, 0).add(entity.getForward()));
        orb.shoot(entity.getLookAngle());
        orb.setDeltaMovement(orb.getDeltaMovement().add(0, 0.2, 0));
        orb.setExplosionRadius(3.5f);
        level.addFreshEntity(orb);
        super.onCast(level, spellLevel, entity, castSource, playerMagicData);
    }

    @Override
    public CastType getCastType() {
        return CastType.LONG;
    }

    @Override
    public Optional<SoundEvent> getCastFinishSound() {
        return Optional.of(SoundEvents.FIRECHARGE_USE);
    }

    @Override
    public AnimationHolder getCastFinishAnimation() {
        return SpellAnimations.SPIT_FINISH_ANIMATION;
    }

}

package com.github.timepsilon.ironsspellbooks.spells;

import com.github.timepsilon.Core;
import com.github.timepsilon.config.STCConfigServer;
import com.github.timepsilon.ironsspellbooks.ModSchoolRegistry;
import com.github.timepsilon.mobeffect.ModMobEffects;
import com.github.timepsilon.particle.ModParticles;
import com.github.timepsilon.stonksevent.StonksEventType;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import java.util.List;

public class HotPotato extends AbstractSpell {

    private final DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.EPIC)
            .setSchoolResource(ModSchoolRegistry.TIME_RESSOURCE)
            .setMaxLevel(1)
            .setCooldownSeconds(3600)
            .build();

    @Override
    public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        return List.of(
                Component.translatable("ui.irons_spellbooks.effect_length", Utils.timeFromTicks(300 * 20, 1))
        );
    }

    private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath(Core.MODID, "hot_potato");

    public HotPotato() {
        this.manaCostPerLevel = 0;
        this.baseSpellPower = 0;
        this.spellPowerPerLevel = 0;
        this.castTime = 20;
        this.baseManaCost = 250;
    }

    @Override
    public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
        if (entity instanceof ServerPlayer serverPlayer) {
            serverPlayer.addEffect(new MobEffectInstance(
                    ModMobEffects.HOT_POTATO,
                    300*20,
                    0,
                    false,
                    true));

        }
        MagicManager.spawnParticles(level, ModParticles.TIME_PARTICLES.get(), entity.getX(), entity.getY(), entity.getZ(),
                20, entity.getBbWidth()*2, entity.getBbHeight()*1.5, entity.getBbWidth()*2, 0, true);
        MagicManager.spawnParticles(level, ParticleTypes.FLAME, entity.getX(), entity.getY(), entity.getZ(),
                100, entity.getBbWidth(), entity.getBbHeight(), entity.getBbWidth(), 0.5, true);

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

}

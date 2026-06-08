package com.github.timepsilon.ironsspellbooks.spells;

import com.github.timepsilon.Core;
import com.github.timepsilon.config.STCConfigServer;
import com.github.timepsilon.ironsspellbooks.ModSchoolRegistry;
import com.github.timepsilon.mobeffect.ModMobEffects;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.Utils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;

public class GrowthSpurt extends AbstractPotionSpell {

    private final DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.RARE)
            .setSchoolResource(ModSchoolRegistry.TIME_RESSOURCE)
            .setMaxLevel(3)
            .setCooldownSeconds(300)
            .build();

    @Override
    public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        return List.of(
                Component.translatable("ui.irons_spellbooks.radius", 3.5f),
                Component.translatable("ui.stonkstimecore.size", Utils.stringTruncation(getSize(spellLevel, caster), 1)),
                Component.translatable("ui.irons_spellbooks.effect_length", Utils.timeFromTicks(30 * 20, 1))
        );
    }

    private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath(Core.MODID, "growth_spurt");

    public GrowthSpurt() {
        super(ModMobEffects.GROWTH_SPURT, 30);
        this.castTime = 20*3;
        this.baseManaCost = 120;
        this.manaCostPerLevel = 50;
    }

    @Override
    public ResourceLocation getSpellResource() {
        return spellId;
    }

    @Override
    public DefaultConfig getDefaultConfig() {
        return defaultConfig;
    }

    public float getSize(int spellLevel, LivingEntity caster) {
        return 2f + 2 * (float) STCConfigServer.CONFIG.SRE_GROWTH_SPURT_FACTOR.getAsDouble() * spellLevel;
    }

}

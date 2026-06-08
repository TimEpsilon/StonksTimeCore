package com.github.timepsilon.ironsspellbooks.spells;

import com.github.timepsilon.Core;
import com.github.timepsilon.ironsspellbooks.ModSchoolRegistry;
import com.github.timepsilon.mobeffect.ModMobEffects;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.spells.*;
import io.redspace.ironsspellbooks.api.util.Utils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;

public class Mirror extends AbstractPotionSpell {

    private final DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.UNCOMMON)
            .setSchoolResource(ModSchoolRegistry.TIME_RESSOURCE)
            .setMaxLevel(1)
            .setCooldownSeconds(180)
            .build();

    @Override
    public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        return List.of(
                Component.translatable("ui.irons_spellbooks.radius", 3.5f),
                Component.translatable("ui.irons_spellbooks.effect_length", Utils.timeFromTicks(60 * 20, 1))
        );
    }

    private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath(Core.MODID, "mirror");

    public Mirror() {
        super(ModMobEffects.MIRROR, 60);
        this.castTime = 20*2;
        this.baseManaCost = 70;
        this.manaCostPerLevel = 0;
    }

    @Override
    public ResourceLocation getSpellResource() {
        return spellId;
    }

    @Override
    public DefaultConfig getDefaultConfig() {
        return defaultConfig;
    }

}

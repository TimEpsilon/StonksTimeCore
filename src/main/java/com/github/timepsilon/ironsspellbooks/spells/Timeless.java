package com.github.timepsilon.ironsspellbooks.spells;

import com.github.timepsilon.Core;
import com.github.timepsilon.config.STCConfigServer;
import com.github.timepsilon.ironsspellbooks.ModSchoolRegistry;
import com.github.timepsilon.mobeffect.ModMobEffects;
import com.github.timepsilon.utils.TimeUtils;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;

public class Timeless extends AbstractPotionSpell {

    private final DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.EPIC)
            .setSchoolResource(ModSchoolRegistry.TIME_RESSOURCE)
            .setMaxLevel(3)
            .setCooldownSeconds(3600)
            .build();

    @Override
    public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        return List.of(
                Component.translatable("ui.irons_spellbooks.radius", 3.5f),
                Component.literal(Utils.stringTruncation(getAmountPerDamage(spellLevel,caster), 1)).withStyle(ChatFormatting.DARK_GREEN)
                        .append(Component.literal("\u9000 ").withStyle(ChatFormatting.WHITE))
                        .append(Component.translatable("ui.stonkstimecore.timeless").withStyle(ChatFormatting.DARK_GREEN))
        );
    }

    private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath(Core.MODID, "timeless");

    public Timeless() {
        super(ModMobEffects.TIMELESS, 180);
        this.castTime = 20*4;
        this.baseManaCost = 180;
        this.manaCostPerLevel = 60;
    }

    @Override
    public ResourceLocation getSpellResource() {
        return spellId;
    }

    @Override
    public DefaultConfig getDefaultConfig() {
        return defaultConfig;
    }

    public int getAmountPerDamage(int spellLevel, LivingEntity caster) {
        return STCConfigServer.CONFIG.SRE_TIMELESS_LOSS.getAsInt() * spellLevel;
    }

}

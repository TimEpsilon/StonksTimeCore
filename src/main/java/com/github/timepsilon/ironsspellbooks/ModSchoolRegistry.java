package com.github.timepsilon.ironsspellbooks;

import com.github.timepsilon.Core;
import com.github.timepsilon.attributes.ModAttributes;
import com.github.timepsilon.sounds.ModSounds;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.SchoolType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.damagesource.DamageType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.awt.*;


public class ModSchoolRegistry {

    private static final DeferredRegister<SchoolType> SCHOOLS = DeferredRegister.create(SchoolRegistry.SCHOOL_REGISTRY_KEY, Core.MODID);

    public static final ResourceLocation TIME_RESSOURCE = ResourceLocation.fromNamespaceAndPath(Core.MODID, "time");
    public static final ResourceKey<DamageType> TIME_DAMAGE = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(Core.MODID, "time"));

    public static final DeferredHolder<SchoolType, SchoolType> TIME = registerSchool(new SchoolType(
            TIME_RESSOURCE,
            ItemTags.create(ResourceLocation.fromNamespaceAndPath(Core.MODID, "time_focus")),
            Component.translatable("school.stonkstimecore.time").withStyle(Style.EMPTY.withColor(Color.decode("#5efff2").getRGB())),
            ModAttributes.TIME_POWER,
            ModAttributes.TIME_DEFENSE,
            ModSounds.TIME_OUT,
            TIME_DAMAGE));

    public static void register(IEventBus eventBus) {
        SCHOOLS.register(eventBus);
    }

    public static DeferredHolder<SchoolType, SchoolType> registerSchool(SchoolType schoolType) {
        return SCHOOLS.register(schoolType.getId().getPath(), () -> schoolType);
    }

}

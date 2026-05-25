package com.github.timepsilon.pehkui;

import com.github.timepsilon.Core;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import virtuoel.pehkui.api.ScaleModifier;
import virtuoel.pehkui.api.ScaleRegistries;

public class ModPehkuiModifier {

    public static final ScaleModifier GROWTH_SPURT = ScaleRegistries.register(
            ScaleRegistries.SCALE_MODIFIERS,
            ResourceLocation.fromNamespaceAndPath(Core.MODID, "growth_spurt"),
            new GrowthSpurtScaleModifier()
    );

    public static final ScaleModifier SHRINKFLATION = ScaleRegistries.register(
            ScaleRegistries.SCALE_MODIFIERS,
            ResourceLocation.fromNamespaceAndPath(Core.MODID, "shrinkflation"),
            new ShrinkflationScaleModifier()
    );

    public static void register() {};
}

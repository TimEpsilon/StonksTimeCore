package com.github.timepsilon.enumextensions;

import com.github.timepsilon.Core;
import net.minecraft.client.gui.Gui;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.common.asm.enumextension.EnumProxy;

public class ModHeartTypes {

    public static final EnumProxy<Gui.HeartType> LIFELINK_HEART = new EnumProxy<>(
            Gui.HeartType.class,
            ResourceLocation.fromNamespaceAndPath(Core.MODID, "hud/heart/lifelink_full"),
            ResourceLocation.fromNamespaceAndPath(Core.MODID, "hud/heart/lifelink_full_blinking"),
            ResourceLocation.fromNamespaceAndPath(Core.MODID, "hud/heart/lifelink_half"),
            ResourceLocation.fromNamespaceAndPath(Core.MODID, "hud/heart/lifelink_half_blinking"),
            ResourceLocation.fromNamespaceAndPath(Core.MODID, "hud/heart/lifelink_full"),
            ResourceLocation.fromNamespaceAndPath(Core.MODID, "hud/heart/lifelink_full_blinking"),
            ResourceLocation.fromNamespaceAndPath(Core.MODID, "hud/heart/lifelink_half"),
            ResourceLocation.fromNamespaceAndPath(Core.MODID, "hud/heart/lifelink_half_blinking")
    );

    public static final EnumProxy<Gui.HeartType> TIMELESS_HEART = new EnumProxy<>(
            Gui.HeartType.class,
            ResourceLocation.fromNamespaceAndPath(Core.MODID, "hud/heart/timeless_full"),
            ResourceLocation.fromNamespaceAndPath(Core.MODID, "hud/heart/timeless_full_blinking"),
            ResourceLocation.fromNamespaceAndPath(Core.MODID, "hud/heart/timeless_half"),
            ResourceLocation.fromNamespaceAndPath(Core.MODID, "hud/heart/timeless_half_blinking"),
            ResourceLocation.fromNamespaceAndPath(Core.MODID, "hud/heart/timeless_full"),
            ResourceLocation.fromNamespaceAndPath(Core.MODID, "hud/heart/timeless_full_blinking"),
            ResourceLocation.fromNamespaceAndPath(Core.MODID, "hud/heart/timeless_half"),
            ResourceLocation.fromNamespaceAndPath(Core.MODID, "hud/heart/timeless_half_blinking")
    );


}

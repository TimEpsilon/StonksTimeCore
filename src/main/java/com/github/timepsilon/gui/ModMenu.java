package com.github.timepsilon.gui;

import com.github.timepsilon.Core;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.builders.MenuBuilder;
import com.tterrag.registrate.util.entry.MenuEntry;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.world.inventory.AbstractContainerMenu;

public class ModMenu {

    public static final MenuEntry<StonksTemporalChronoscopeMenu> STONKS_TEMPORAL_CHRONOSCOPE = register(
            "stonks_temporal_chronoscope",
            StonksTemporalChronoscopeMenu::new,
            () -> StonksTemporalChronoscopeScreen::new
    );

    private static <C extends AbstractContainerMenu, S extends Screen & MenuAccess<C>> MenuEntry<C> register(
            String name, MenuBuilder.ForgeMenuFactory<C> factory, NonNullSupplier<MenuBuilder.ScreenFactory<C, S>> screenFactory) {
        return Core.REGISTRATE
                .menu(name, factory, screenFactory)
                .register();
    }

    public static void register() {}

}

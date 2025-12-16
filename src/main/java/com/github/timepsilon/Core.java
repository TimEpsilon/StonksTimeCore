package com.github.timepsilon;

import com.github.timepsilon.block.ModBlocks;
import com.github.timepsilon.block.entity.ModBlockEntities;
import com.github.timepsilon.entity.ModEntities;
import com.github.timepsilon.events.handlers.ModEventsManager;
import com.github.timepsilon.events.handlers.NeoForgeEventsManager;
import com.github.timepsilon.gui.ModMenu;
import com.github.timepsilon.gui.packets.ModPackets;
import com.github.timepsilon.items.ModItems;
import com.github.timepsilon.time.TickHook;
import com.mojang.logging.LogUtils;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.item.KineticStats;
import com.simibubi.create.foundation.item.TooltipModifier;
import net.createmod.catnip.lang.FontHelper;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;

@Mod(Core.MODID)
public class Core {

    public static final String MODID = "stonkstimecore";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(Core.MODID);


    public Core(IEventBus modEventBus, ModContainer modContainer) {

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Allows this class to listen to events
        //NeoForge.EVENT_BUS.register(this);

        REGISTRATE.registerEventListeners(modEventBus); // This first else it crashes

        // Register
        ModItems.register();
        ModBlocks.register();
        ModBlockEntities.register();
        ModMenu.register();
        ModPackets.register();

        ModEntities.register(modEventBus);

        // Register Neoforge Events
        NeoForge.EVENT_BUS.register(new NeoForgeEventsManager());
        NeoForge.EVENT_BUS.register(new TickHook());

        // Register Events
        modEventBus.register(new ModEventsManager());

    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("Started loading StonksTimeCore...");
    }

    static {
        // Stress tooltip
        REGISTRATE.setTooltipModifierFactory(item -> {
           return new ItemDescription.Modifier(item, FontHelper.Palette.STANDARD_CREATE)
                   .andThen(TooltipModifier.mapNull(KineticStats.create(item)));
        });
    }


}

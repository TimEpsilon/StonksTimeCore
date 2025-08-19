package com.github.timepsilon;

import com.github.timepsilon.server.events.ModEventsManager;
import com.github.timepsilon.server.events.NeoForgeEventsManager;
import com.github.timepsilon.server.items.ModItems;
import com.mojang.logging.LogUtils;
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

    // Registry
    //public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);
    //public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public Core(IEventBus modEventBus, ModContainer modContainer) {

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Allows this class to listen to events
        //NeoForge.EVENT_BUS.register(this);

        // Register
        ModItems.register(modEventBus);
        //BLOCKS.register(modEventBus);
        //CREATIVE_MODE_TABS.register(modEventBus);

        // Register Neoforge Events
        NeoForge.EVENT_BUS.register(new NeoForgeEventsManager());

        // Register Events
        modEventBus.register(new ModEventsManager());


    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("Started loading StonksTimeCore...");
    }


}

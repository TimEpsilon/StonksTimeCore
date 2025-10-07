package com.github.timepsilon.events;

import com.github.timepsilon.Core;
import com.github.timepsilon.create.STCPartialModels;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.common.NeoForge;

@Mod(value = Core.MODID, dist = Dist.CLIENT)
public class STCClient {

    public STCClient(IEventBus eventBus) {onSTCClient(eventBus);}

    public static void onSTCClient(IEventBus eventBus) {
        IEventBus neoEventBus = NeoForge.EVENT_BUS;

        eventBus.addListener(STCClient::clientInit);

    }

    public static void clientInit(final FMLClientSetupEvent event) {
        STCPartialModels.init();
        Core.LOGGER.info("Registered partial models!");
    }
}

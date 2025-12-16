package com.github.timepsilon.events.handlers;

import com.github.timepsilon.Core;
import com.github.timepsilon.create.STCPartialModels;
import com.github.timepsilon.entity.ModEntities;
import com.github.timepsilon.entity.client.TimeGearRenderer;
import com.github.timepsilon.gui.overlay.TimerOverlay;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.common.NeoForge;

@Mod(value = Core.MODID, dist = Dist.CLIENT)
public class ClientEventsManager {

    public ClientEventsManager(IEventBus eventBus) {onSTCClient(eventBus);}

    public static void onSTCClient(IEventBus eventBus) {
        IEventBus neoEventBus = NeoForge.EVENT_BUS;
        eventBus.addListener(ClientEventsManager::clientInit);
        eventBus.addListener(ClientEventsManager::onRegisterOverlay);

    }

    public static void clientInit(final FMLClientSetupEvent event) {
        STCPartialModels.init();
        Core.LOGGER.info("Registered partial models!");

        EntityRenderers.register(ModEntities.TIME_GEAR.get(), TimeGearRenderer::new);
    }

    public static void onRegisterOverlay(RegisterGuiLayersEvent event) {
        event.registerAbove(VanillaGuiLayers.TITLE, ResourceLocation.fromNamespaceAndPath(Core.MODID,"timer_overlay"), TimerOverlay.instance);
    }
}

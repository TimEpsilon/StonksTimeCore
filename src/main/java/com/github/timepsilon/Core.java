package com.github.timepsilon;

import com.github.timepsilon.block.ModBlocks;
import com.github.timepsilon.block.entity.ModBlockEntities;
import com.github.timepsilon.block.entity.client.StonksTemporalChronoscopeRenderer;
import com.github.timepsilon.events.ModEventsManager;
import com.github.timepsilon.events.NeoForgeEventsManager;
import com.github.timepsilon.items.ModItems;
import com.mojang.logging.LogUtils;
import com.simibubi.create.foundation.data.CreateRegistrate;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;

@Mod(Core.MODID)
public class Core {

    public static final String MODID = "stonkstimecore";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(Core.MODID);

    // Registry
    //public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public Core(IEventBus modEventBus, ModContainer modContainer) {

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Allows this class to listen to events
        //NeoForge.EVENT_BUS.register(this);

        // Register
        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        //CREATIVE_MODE_TABS.register(modEventBus);

        // Register Neoforge Events
        NeoForge.EVENT_BUS.register(new NeoForgeEventsManager());

        // Register Events
        modEventBus.register(new ModEventsManager());

        REGISTRATE.registerEventListeners(modEventBus);

    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("Started loading StonksTimeCore...");
        event.enqueueWork(() -> {
            ModBlocks.registerStress();
        });
    }

    @EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(final FMLClientSetupEvent event) {
            BlockEntityRenderers.register(ModBlockEntities.STONKS_TEMPORAL_CHRONOSCOPE_ENTITY.get(), StonksTemporalChronoscopeRenderer::new);
        }
    }


}

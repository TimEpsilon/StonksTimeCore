package com.github.timepsilon;

import com.github.timepsilon.block.ModBlocks;
import com.github.timepsilon.block.entity.ModBlockEntities;
import com.github.timepsilon.client.gui.ModMenu;
import com.github.timepsilon.config.STCConfigClient;
import com.github.timepsilon.config.STCConfigServer;
import com.github.timepsilon.entity.ModEntities;
import com.github.timepsilon.items.ModItems;
import com.github.timepsilon.loot.ModLoot;
import com.github.timepsilon.packets.ModPackets;
import com.github.timepsilon.sounds.ModSounds;
import com.mojang.logging.LogUtils;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.item.KineticStats;
import com.simibubi.create.foundation.item.TooltipModifier;
import net.createmod.catnip.lang.FontHelper;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import org.slf4j.Logger;

@Mod(Core.MODID)
public class Core {

    public static final String MODID = "stonkstimecore";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(Core.MODID);
    private static Database database;
    private static StatsService statsService;

    public Core(IEventBus modEventBus, ModContainer modContainer) {

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Allows this class to listen to events
        //NeoForge.EVENT_BUS.register(this);

        REGISTRATE.registerEventListeners(modEventBus); // This first else it crashes

        // Register
        ModItems.register();
        ModBlocks.register();
        ModMenu.register();
        ModPackets.register();
        ModBlockEntities.register();
        ModEntities.register(modEventBus);
        ModSounds.register(modEventBus);
        ModLoot.register(modEventBus);

        // Config register
        modContainer.registerConfig(ModConfig.Type.SERVER, STCConfigServer.CONFIG_SPEC);

        if (FMLEnvironment.dist == Dist.CLIENT) {
            modContainer.registerConfig(ModConfig.Type.CLIENT, STCConfigClient.CONFIG_SPEC);
            modContainer.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
        }
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("Started loading StonksTimeCore...");

        // Stats
        database = new Database();
        database.connect();

        statsService = new StatsService(database);
        statsService.start();
        NeoForge.EVENT_BUS.register(new StatsEventHandler(statsService));
    }

    private void shutdown() {
        statsService.shutdown();
        database.close();
    }

    public static StatsService stats() {
        return statsService;
    }

    static {
        // Stress tooltip
        REGISTRATE.setTooltipModifierFactory(item -> {
           return new ItemDescription.Modifier(item, FontHelper.Palette.STANDARD_CREATE)
                   .andThen(TooltipModifier.mapNull(KineticStats.create(item)));
        });
    }


}

package com.github.timepsilon.items;

import com.github.timepsilon.Core;
import com.github.timepsilon.block.ModBlocks;
import com.github.timepsilon.items.custom.StonksTemporalChronoscopeItem;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {

    // The Registry
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Core.MODID);

    // The items
    public static final DeferredItem<Item> MCOIN_1 = createItem("mcoin_1");
    public static final DeferredItem<Item> MCOIN_10 = createItem("mcoin_10");
    public static final DeferredItem<Item> MCOIN_50 = createItem("mcoin_50");
    public static final DeferredItem<Item> MCOIN_100 = createItem("mcoin_100");
    public static final DeferredItem<Item> MCOIN_500 = createItem("mcoin_500");
    public static final DeferredItem<Item> MCOIN_10000 = createItem("mcoin_10000");

    // Block Items
    public static final DeferredItem<Item> STONKS_TEMPORAL_CHRONOSCOPE = ITEMS.register("stonks_temporal_chronoscope",
            () -> new StonksTemporalChronoscopeItem(ModBlocks.STONKS_TEMPORAL_CHRONOSCOPE.get(), new Item.Properties()));

    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
    }

    private static DeferredItem<Item> createItem(String name) {
        return ITEMS.register(name, () -> new Item(new Item.Properties()));
    }

}

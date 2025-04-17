package com.github.timepsilon.server.items;

import com.github.timepsilon.Core;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {

    // The Registry
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Core.MODID);

    // The items
    public static final DeferredItem<Item> MCOIN_1 = ITEMS.register("mcoin_1", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> MCOIN_10 = ITEMS.register("mcoin_10", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> MCOIN_50 = ITEMS.register("mcoin_50", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> MCOIN_100 = ITEMS.register("mcoin_100", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> MCOIN_500 = ITEMS.register("mcoin_500", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> MCOIN_10000 = ITEMS.register("mcoin_10000", () -> new Item(new Item.Properties()));

    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
    }

    private static DeferredItem<Item> createItem(String name) {
        return ITEMS.register(name, () -> new Item(new Item.Properties()));
    }

}

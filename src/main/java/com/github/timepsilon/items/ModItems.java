package com.github.timepsilon.items;

import com.github.timepsilon.Core;
import com.github.timepsilon.items.custom.TimeGearItem;
import com.simibubi.create.foundation.data.AssetLookup;
import com.tterrag.registrate.util.entry.ItemEntry;
import dev.ithundxr.createnumismatics.registry.NumismaticsCreativeModeTabs;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

public class ModItems {

    static {
        Core.REGISTRATE.defaultCreativeTab((ResourceKey<CreativeModeTab>) null);
    }

    // The items
    public static final ItemEntry<TimeGearItem> TIME_GEAR = Core.REGISTRATE.item("time_gear", TimeGearItem::new)
            .properties(p -> p.stacksTo(1))
            .model(AssetLookup.itemModelWithPartials())
            .properties(p -> p.rarity(Rarity.EPIC))
            .tab(NumismaticsCreativeModeTabs.getBaseTabKey())
            .register();

    public static final ItemEntry<Item> GOLDEN_TICKET = Core.REGISTRATE.item("golden_ticket", Item::new)
            .properties(p -> p.stacksTo(64))
            .properties(p -> p.rarity(Rarity.EPIC))
            .tab(NumismaticsCreativeModeTabs.getBaseTabKey())
            .register();

    public static void register() {}

}

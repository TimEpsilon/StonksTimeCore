package com.github.timepsilon.items;

import com.github.timepsilon.Core;
import com.github.timepsilon.items.custom.TimeGearItem;
import com.simibubi.create.AllCreativeModeTabs;
import com.simibubi.create.AllTags;
import com.simibubi.create.content.equipment.wrench.WrenchItem;
import com.simibubi.create.foundation.data.AssetLookup;
import com.tterrag.registrate.util.entry.ItemEntry;
import dev.ithundxr.createnumismatics.registry.NumismaticsCreativeModeTabs;
import dev.ithundxr.createnumismatics.registry.neoforge.NumismaticsCreativeModeTabsImpl;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {

    static {
        Core.REGISTRATE.setCreativeTab(NumismaticsCreativeModeTabsImpl.MAIN_TAB);
    }

    // The items
    public static final ItemEntry<TimeGearItem> TIME_GEAR = Core.REGISTRATE.item("time_gear", TimeGearItem::new)
            .properties(p -> p.stacksTo(1))
            .model(AssetLookup.itemModelWithPartials())
            .properties(p -> p.rarity(Rarity.EPIC))
            .register();

    public static void register() {}

}

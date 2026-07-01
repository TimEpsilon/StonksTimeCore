package com.github.timepsilon.datamaps;

import com.github.timepsilon.Core;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;

import java.util.HashMap;

@EventBusSubscriber(modid = Core.MODID)
public class SCTManager {

    public static HashMap<Item, Float> SCT_MAPS = new HashMap<>();

    private static HashMap<Item,Float> getSCTHashMap(RegistryAccess registry) {
        HashMap<Item,Float> map = new HashMap<>();
        Registry<Item> registryItem = registry.registryOrThrow(Registries.ITEM);
        for (Item item : registryItem) {
            SCTMap sct = item.builtInRegistryHolder().getData(DataMaps.SCT_MAP);
            if (sct != null) {
                map.put(item, sct.SCT());
            }
        }
        return map;
    }

    @SubscribeEvent
    public static void onDataReload(OnDatapackSyncEvent event) {
        SCT_MAPS = getSCTHashMap(event.getPlayerList().getServer().registryAccess());
    }
}

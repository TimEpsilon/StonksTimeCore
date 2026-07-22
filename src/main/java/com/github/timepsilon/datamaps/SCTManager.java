package com.github.timepsilon.datamaps;

import com.github.timepsilon.Core;
import com.github.timepsilon.config.STCConfigServer;
import com.github.timepsilon.database.SCTTransactionDatabase;
import com.github.timepsilon.utils.SCTMathUtils;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@EventBusSubscriber(modid = Core.MODID)
public class SCTManager {

    public static HashMap<Item, Float> SCT_MAP = new HashMap<>();
    public static HashMap<Item, Integer> AMOUNT_MAP = new HashMap<>();

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

    public static void updateSCTMap() {
        Map<String, Integer> sold = SCTTransactionDatabase.getDatabase()
                .getAmountsSoldByItem(STCConfigServer.CONFIG.SCT_REDUCTION_TIME.getAsInt());

        for (Item item : SCT_MAP.keySet()) {
            if (sold.containsKey(item.toString())) {
                float newValue = SCTMathUtils.currentPrice(SCT_MAP.get(item), sold.get(item.toString()));
                SCT_MAP.put(item, newValue);
                AMOUNT_MAP.put(item, sold.get(item.toString()));
            }
        }
    }

    public static void updateSCTMap(Collection<Item> items) {
        Map<String, Integer> sold = SCTTransactionDatabase.getDatabase()
                .getAmountsSoldForItems(items.stream().map(Item::toString).toList(), STCConfigServer.CONFIG.SCT_REDUCTION_TIME.getAsInt());

        for (Item item : items) {
            if (sold.containsKey(item.toString()) && SCT_MAP.containsKey(item)) {
                float newValue = SCTMathUtils.currentPrice(SCT_MAP.get(item), sold.get(item.toString()));
                SCT_MAP.put(item, newValue);
                AMOUNT_MAP.put(item, sold.get(item.toString()));
            }
        }
    }

    @SubscribeEvent
    public static void onDataReload(OnDatapackSyncEvent event) {
        SCT_MAP = getSCTHashMap(event.getPlayerList().getServer().registryAccess());
        updateSCTMap();
    }
}

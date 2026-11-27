package com.github.timepsilon.datamaps;

import com.github.timepsilon.Core;
import com.github.timepsilon.config.STCConfigServer;
import com.github.timepsilon.database.SCTTransactionDatabase;
import com.github.timepsilon.packets.server.SCTSyncPacket;
import com.github.timepsilon.utils.SCTMathUtils;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerPlayer;
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

        refreshPrices(SCT_MAP.keySet(), sold);
    }

    public static void updateSCTMap(Collection<Item> items) {
        Map<String, Integer> sold = SCTTransactionDatabase.getDatabase()
                .getAmountsSoldForItems(items.stream().map(Item::toString).toList(), STCConfigServer.CONFIG.SCT_REDUCTION_TIME.getAsInt());

        refreshPrices(items, sold);
    }

    private static void refreshPrices(Collection<Item> items, Map<String, Integer> sold) {
        SCTPriceRefresher.refresh(items, sold, SCTManager::getBaseSCT, SCTMathUtils::currentPrice, SCT_MAP, AMOUNT_MAP);
        syncToAll();
    }

    /** The SCT value defined by the data map, before any price reduction. */
    private static Float getBaseSCT(Item item) {
        SCTMap sct = item.builtInRegistryHolder().getData(DataMaps.SCT_MAP);
        return sct == null ? null : sct.SCT();
    }

    public static void syncToPlayer(ServerPlayer player) {
        Core.LOGGER.debug("Sent data to player");
        CatnipServices.NETWORK.sendToClient(player, SCTSyncPacket.fromCurrentMap());
    }

    public static void syncToAll() {
        Core.LOGGER.debug("Sent data to all");
        CatnipServices.NETWORK.sendToAllClients(SCTSyncPacket.fromCurrentMap());
    }

    @SubscribeEvent
    public static void onDataReload(OnDatapackSyncEvent event) {
        SCT_MAP = getSCTHashMap(event.getPlayerList().getServer().registryAccess());
        updateSCTMap();
        event.getRelevantPlayers().forEach(SCTManager::syncToPlayer);
    }
}

package com.github.timepsilon.server.datamaps;

import com.github.timepsilon.Core;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.datamaps.DataMapType;

public class DataMaps {

    public static final DataMapType<Item, SCTMap> SCT_MAP = DataMapType.builder(
                    ResourceLocation.fromNamespaceAndPath(Core.MODID, "sct"),
                    Registries.ITEM,
                    SCTMap.CODEC
            ).synced(
                    SCTMap.CODEC,
                    false
            )
            .build();

}

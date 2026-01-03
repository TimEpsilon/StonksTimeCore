package com.github.timepsilon.create;

import com.github.timepsilon.Core;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.minecraft.resources.ResourceLocation;

public class STCPartialModels {

    public static final PartialModel

    GYROSCOPE_OUTER_RING = block("stonks_temporal_chronoscope/outer_ring"),
    GYROSCOPE_INNER_RING = block("stonks_temporal_chronoscope/inner_ring"),
    GYROSCOPE_TIME_GEAR = block("stonks_temporal_chronoscope/time_gear")
    ;

    private static PartialModel block(String path) {
        return PartialModel.of(ResourceLocation.fromNamespaceAndPath(Core.MODID, "block/" + path));
    }

    public static void init() {
        // init static fields
    }
}

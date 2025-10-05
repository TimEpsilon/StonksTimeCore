package com.github.timepsilon.block;

import com.github.timepsilon.block.custom.StonksTemporalChronoscope;
import com.simibubi.create.api.stress.BlockStressValues;
import com.simibubi.create.foundation.data.AssetLookup;
import com.simibubi.create.foundation.data.SharedProperties;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.world.level.material.MapColor;

import static com.github.timepsilon.Core.REGISTRATE;
import static com.simibubi.create.foundation.data.ModelGen.customItemModel;
import static com.simibubi.create.foundation.data.TagGen.pickaxeOnly;


public class ModBlocks {

    public static void register() {}

    public static final BlockEntry<StonksTemporalChronoscope> STONKS_TEMPORAL_CHRONOSCOPE = REGISTRATE
            .block("stonks_temporal_chronoscope", StonksTemporalChronoscope::new)
            .initialProperties(SharedProperties::softMetal)
            .properties(p -> p.mapColor(MapColor.TERRACOTTA_YELLOW))
            .transform(pickaxeOnly())
            .blockstate((c, p) -> p.simpleBlock(c.getEntry(), AssetLookup.partialBaseModel(c , p)))
            .onRegister(block -> BlockStressValues.IMPACTS.register(block, () -> 4608d))
            .item()
            .transform(customItemModel())
            .register();
}

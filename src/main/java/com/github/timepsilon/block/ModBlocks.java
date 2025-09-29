package com.github.timepsilon.block;

import com.github.timepsilon.Core;
import com.github.timepsilon.block.custom.BankBlock;
import com.github.timepsilon.block.custom.StonksTemporalChronoscope;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.api.stress.BlockStressValues;
import com.simibubi.create.content.kinetics.millstone.MillstoneBlock;
import com.simibubi.create.foundation.data.AssetLookup;
import com.simibubi.create.foundation.data.BlockStateGen;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.data.SharedProperties;
import com.simibubi.create.infrastructure.config.CStress;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.github.timepsilon.Core.REGISTRATE;
import static com.simibubi.create.foundation.data.BlockStateGen.axisBlock;
import static com.simibubi.create.foundation.data.ModelGen.customItemModel;
import static com.simibubi.create.foundation.data.TagGen.pickaxeOnly;


public class ModBlocks {

    // The Registry
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Core.MODID);

    // Register
    public static void register(IEventBus modEventBus) {
        BLOCKS.register(modEventBus);
    }

    // The blocks
    public static final DeferredBlock<Block> STONKS_TEMPORAL_CHRONOSCOPE = BLOCKS.register("stonks_temporal_chronoscope",
            () -> new StonksTemporalChronoscope(
                    BlockBehaviour
                            .Properties
                            .ofFullCopy(AllBlocks.MECHANICAL_ARM.get())
            ));

    // Stress impact
    public static void registerStress() {
        BlockStressValues.IMPACTS.register(STONKS_TEMPORAL_CHRONOSCOPE.get(), () -> 4608d);
    }

    public static final BlockEntry<BankBlock> BANK = REGISTRATE
            .block("bank", BankBlock::new)
            .initialProperties(SharedProperties::softMetal)
            .properties(p -> p.mapColor(MapColor.TERRACOTTA_YELLOW))
            .transform(pickaxeOnly())
            .blockstate((c, p) -> p.simpleBlock(c.getEntry(), AssetLookup.partialBaseModel(c , p))) // This crashes
            .onRegister(block -> BlockStressValues.IMPACTS.register(block, () -> 16))
            .item()
            .transform(customItemModel())
            .register();
}

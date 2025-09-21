package com.github.timepsilon.block;

import com.github.timepsilon.Core;
import com.github.timepsilon.block.custom.StonksTemporalChronoscope;
import com.simibubi.create.AllBlocks;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;


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
}

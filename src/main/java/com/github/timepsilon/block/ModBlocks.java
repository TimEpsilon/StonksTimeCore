package com.github.timepsilon.block;

import com.github.timepsilon.Core;
import com.github.timepsilon.block.custom.SlotMachine;
import com.github.timepsilon.block.custom.StonksTemporalChronoscope;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.api.stress.BlockStressValues;
import com.simibubi.create.foundation.data.SharedProperties;
import com.tterrag.registrate.util.entry.BlockEntry;
import dev.ithundxr.createnumismatics.registry.NumismaticsCreativeModeTabs;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.github.timepsilon.Core.REGISTRATE;
import static com.simibubi.create.foundation.data.ModelGen.customItemModel;
import static com.simibubi.create.foundation.data.TagGen.pickaxeOnly;


public class ModBlocks {

    public static void register() {
    }

    public static final BlockEntry<StonksTemporalChronoscope> STONKS_TEMPORAL_CHRONOSCOPE = REGISTRATE
            .block("stonks_temporal_chronoscope", StonksTemporalChronoscope::new)
            .initialProperties(SharedProperties::softMetal)
            .properties(p -> p.mapColor(MapColor.TERRACOTTA_YELLOW))
            .transform(pickaxeOnly())
            .onRegister(block -> BlockStressValues.IMPACTS.register(block, () -> 9284d)) // 9284 su/rpm * 30 rpm = 278 520 su (max su : 278 528)
            .item()
            .tab(NumismaticsCreativeModeTabs.getBaseTabKey())
            .transform(customItemModel())
            .register();

    public static final BlockEntry<SlotMachine> SLOT_MACHINE = REGISTRATE
            .block("slot_machine", SlotMachine::new)
            .initialProperties(SharedProperties::softMetal)
            .properties(p -> p.mapColor(MapColor.TERRACOTTA_YELLOW).noOcclusion())
            .transform(pickaxeOnly())
            .item()
            .tab(NumismaticsCreativeModeTabs.getBaseTabKey())
            .transform(customItemModel())
            .register();

}

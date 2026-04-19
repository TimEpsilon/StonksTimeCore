package com.github.timepsilon.block.entity;

import com.github.timepsilon.block.ModBlocks;
import com.github.timepsilon.block.entity.client.SlotMachineRenderer;
import com.github.timepsilon.block.entity.client.StonksTemporalChronoscopeRenderer;
import com.github.timepsilon.block.entity.client.StonksTemporalChronoscopeVisual;
import com.github.timepsilon.block.entity.server.SlotMachineEntity;
import com.github.timepsilon.block.entity.server.StonksTemporalChronoscopeEntity;
import com.tterrag.registrate.util.entry.BlockEntityEntry;

import static com.github.timepsilon.Core.REGISTRATE;


public class ModBlockEntities {

    public static void register() {
    }

    public static final BlockEntityEntry<StonksTemporalChronoscopeEntity> STONKS_TEMPORAL_CHRONOSCOPE_ENTITY = REGISTRATE
            .blockEntity("stonks_temporal_chronoscope", StonksTemporalChronoscopeEntity::new)
            .visual(() -> StonksTemporalChronoscopeVisual::new, false)
            .validBlocks(ModBlocks.STONKS_TEMPORAL_CHRONOSCOPE)
            .renderer(() -> StonksTemporalChronoscopeRenderer::new)
            .register();

    public static final BlockEntityEntry<SlotMachineEntity> SLOT_MACHINE_ENTITY = REGISTRATE
            .blockEntity("slot_machine", SlotMachineEntity::new)
            .validBlocks(ModBlocks.SLOT_MACHINE)
            .renderer(() -> SlotMachineRenderer::new)
            .register();

}

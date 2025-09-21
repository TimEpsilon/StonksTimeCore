package com.github.timepsilon.block.entity;

import com.github.timepsilon.Core;
import com.github.timepsilon.block.ModBlocks;
import com.github.timepsilon.block.entity.server.StonksTemporalChronoscopeEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;


public class ModBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, Core.MODID);

    public static void register(IEventBus modEventBus) {
        BLOCK_ENTITIES.register(modEventBus);
    }

    public static final Supplier<BlockEntityType<StonksTemporalChronoscopeEntity>> STONKS_TEMPORAL_CHRONOSCOPE_ENTITY =
            BLOCK_ENTITIES.register("stonks_temporal_chronoscope",
                    () -> BlockEntityType.Builder.of(
                                StonksTemporalChronoscopeEntity::new,
                                ModBlocks.STONKS_TEMPORAL_CHRONOSCOPE.get())
                            .build(null));

}

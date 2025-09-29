package com.github.timepsilon.block.entity;

import com.github.timepsilon.Core;
import com.github.timepsilon.block.ModBlocks;
import com.github.timepsilon.block.entity.client.BankRenderer;
import com.github.timepsilon.block.entity.client.BankVisual;
import com.github.timepsilon.block.entity.server.BankBlockEntity;
import com.github.timepsilon.block.entity.server.StonksTemporalChronoscopeEntity;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.OrientedRotatingVisual;
import com.simibubi.create.content.kinetics.base.SingleAxisRotatingVisual;
import com.simibubi.create.content.kinetics.millstone.MillstoneBlockEntity;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

import static com.github.timepsilon.Core.REGISTRATE;


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

    public static final BlockEntityEntry<BankBlockEntity> BANK = REGISTRATE
            .blockEntity("bank", BankBlockEntity::new)
            .visual(() -> SingleAxisRotatingVisual.of(AllPartialModels.SHAFT_HALF))
            .renderer(() -> BankRenderer::new)
            .validBlocks(ModBlocks.BANK)
            .register();

}

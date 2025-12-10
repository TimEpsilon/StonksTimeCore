package com.github.timepsilon.entity;

import com.github.timepsilon.Core;
import com.github.timepsilon.entity.custom.TimeGearEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModEntities {

    public static final DeferredRegister<EntityType<?>> ENTITIY_TYPES =
            DeferredRegister.create(Registries.ENTITY_TYPE, Core.MODID);

    public static final Supplier<EntityType<TimeGearEntity>> TIME_GEAR =
            ENTITIY_TYPES.register("time_gear",
                    () -> EntityType.Builder.of(TimeGearEntity::new, MobCategory.MISC)
                            .sized(3/16f,3/16f)
                            .eyeHeight(2/32f)
                            .fireImmune()
                            .build("time_gear")
            );

    public static void register(IEventBus bus) {
        ENTITIY_TYPES.register(bus);
    }
}

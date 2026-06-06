package com.github.timepsilon.entity;

import com.github.timepsilon.Core;
import com.github.timepsilon.entity.custom.PotionMagicProjectile;
import com.github.timepsilon.entity.custom.TimeGearEntity;
import net.minecraft.core.registries.Registries;
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
                            .sized(9/16f,9/16f)
                            .eyeHeight(10/32f)
                            .build("time_gear"));

    public static final Supplier<EntityType<PotionMagicProjectile>> POTION_MAGIC_PROJECTILE =
            ENTITIY_TYPES.register("potion_magic_projectile", () -> EntityType.Builder.<PotionMagicProjectile>of(PotionMagicProjectile::new, MobCategory.MISC)
                    .sized(1f, 1f)
                    .clientTrackingRange(4)
                    .build("potion_magic_projectile"));

    public static void register(IEventBus bus) {
        ENTITIY_TYPES.register(bus);
    }
}

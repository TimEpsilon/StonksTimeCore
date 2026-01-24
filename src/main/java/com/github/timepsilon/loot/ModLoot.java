package com.github.timepsilon.loot;

import com.github.timepsilon.Core;
import com.mojang.serialization.MapCodec;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class ModLoot {

    public static final DeferredRegister<MapCodec<? extends IGlobalLootModifier>> GLOBAL_LOOT_MODIFIER_SERIALIZERS =
            DeferredRegister.create(NeoForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, Core.MODID);

    public static final Supplier<MapCodec<TimeGearGlobalLootModifier>> TIME_GEAR_GLM =
            GLOBAL_LOOT_MODIFIER_SERIALIZERS.register("time_gear_glm", () -> TimeGearGlobalLootModifier.CODEC);

    public static void register(IEventBus bus) {
        GLOBAL_LOOT_MODIFIER_SERIALIZERS.register(bus);
    }
}

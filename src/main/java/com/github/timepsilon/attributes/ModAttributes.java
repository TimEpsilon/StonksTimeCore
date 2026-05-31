package com.github.timepsilon.attributes;

import com.github.timepsilon.Core;
import io.redspace.ironsspellbooks.api.attribute.MagicPercentAttribute;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModAttributes {

    public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(
            BuiltInRegistries.ATTRIBUTE, Core.MODID
    );

    public static final Holder<Attribute> SCT_FACTOR = ATTRIBUTES.register("sct_factor", () -> new RangedAttribute(
            "attribute.stonkstimecore.sct_factor",
            1,
            0,
            100
    ));

    public static final Holder<Attribute> TIME_POWER = ATTRIBUTES.register("time_spell_power", () -> new MagicPercentAttribute(
            "attribute.stonkstimecore.time_spell_power",
            1.0D,
            -100,
            100)
            .setSyncable(true));

    public static final Holder<Attribute> TIME_DEFENSE = ATTRIBUTES.register("time_magic_resist", () -> new MagicPercentAttribute(
            "attribute.stonkstimecore.time_magic_resist",
            1.0D,
            -100,
            100)
            .setSyncable(true));



    public static void register(IEventBus bus) {
        ATTRIBUTES.register(bus);
    }

}

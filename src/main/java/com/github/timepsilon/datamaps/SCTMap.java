package com.github.timepsilon.datamaps;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;

import java.util.HashMap;


public record SCTMap(float SCT) {

    public static final Codec<SCTMap> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.FLOAT.fieldOf("SCT").forGetter(SCTMap::SCT)
    ).apply(instance, SCTMap::new));

}

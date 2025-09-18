package com.github.timepsilon.datamaps;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;


public record SCTMap(float SCT) {

    public static final Codec<SCTMap> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.FLOAT.fieldOf("SCT").forGetter(SCTMap::SCT)
    ).apply(instance, SCTMap::new));

}

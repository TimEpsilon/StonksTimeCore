package com.github.timepsilon.server.block.entity.client;

import com.github.timepsilon.Core;
import com.github.timepsilon.server.block.entity.server.StonksTemporalChronoscopeEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class StonksTemporalChronoscopeModel extends GeoModel<StonksTemporalChronoscopeEntity> {

    private final ResourceLocation model = ResourceLocation.fromNamespaceAndPath(Core.MODID, "geo/stc/stonks_temporal_chronoscope.geo.json");
    private final ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(Core.MODID, "textures/block/stonks_temporal_chronoscope.png");
    private final ResourceLocation animations = ResourceLocation.fromNamespaceAndPath(Core.MODID, "animations/stc/spinning.animation.json");

    @Override
    public ResourceLocation getModelResource(StonksTemporalChronoscopeEntity animatable) {
        return model;
    }

    @Override
    public ResourceLocation getTextureResource(StonksTemporalChronoscopeEntity animatable) {
        return texture;
    }

    @Override
    public ResourceLocation getAnimationResource(StonksTemporalChronoscopeEntity animatable) {
        return animations;
    }
}

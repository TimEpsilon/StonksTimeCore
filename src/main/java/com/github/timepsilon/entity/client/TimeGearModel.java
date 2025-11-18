package com.github.timepsilon.entity.client;

import com.github.timepsilon.Core;
import com.github.timepsilon.entity.custom.TimeGearEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.model.GeoModel;

public class TimeGearModel extends GeoModel<TimeGearEntity> {
    @Override
    public ResourceLocation getModelResource(TimeGearEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(Core.MODID, "geo/time_gear.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(TimeGearEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(Core.MODID, "textures/block/stonks_temporal_chronoscope.png");
    }

    @Override
    public ResourceLocation getAnimationResource(TimeGearEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(Core.MODID, "animations/time_gear/clock.animation.json");
    }

    @Override
    public @Nullable RenderType getRenderType(TimeGearEntity animatable, ResourceLocation texture) {
        return RenderType.entityTranslucentCull(texture);
    }
}

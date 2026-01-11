package com.github.timepsilon.entity.client;

import com.github.timepsilon.Core;
import com.github.timepsilon.entity.custom.TimeGearEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;

public class TimeGearModel extends GeoModel<TimeGearEntity> {

    @Override
    public ResourceLocation getModelResource(TimeGearEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(Core.MODID, "geo/time_gear.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(TimeGearEntity animatable) {
        if (animatable.getState().isOut()) {
            return ResourceLocation.fromNamespaceAndPath(Core.MODID, "textures/item/time_gear_out.png");
        } else {
            return ResourceLocation.fromNamespaceAndPath(Core.MODID, "textures/item/time_gear.png");
        }
    }

    @Override
    public ResourceLocation getAnimationResource(TimeGearEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(Core.MODID, "animations/time_gear/clock.animation.json");
    }

    @Override
    public @Nullable RenderType getRenderType(TimeGearEntity animatable, ResourceLocation texture) {
        return RenderType.entityTranslucentCull(texture);
    }

    @Override
    public void setCustomAnimations(TimeGearEntity animatable, long instanceId, AnimationState<TimeGearEntity> animationState) {
        GeoBone head = getAnimationProcessor().getBone("head");

        if (head != null) {
            Entity entity = animationState.getData(DataTickets.ENTITY);
            head.setRotY((entity.getYRot()+90) * Mth.DEG_TO_RAD); // Align animation axis with orientation direction
        } else {
            Core.LOGGER.error("TimeGearModel::setCustomAnimations: No head bone was found in geo file");
        }
    }
}

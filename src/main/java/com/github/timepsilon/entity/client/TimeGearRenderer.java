package com.github.timepsilon.entity.client;

import com.github.timepsilon.Core;
import com.github.timepsilon.entity.custom.TimeGearEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class TimeGearRenderer extends GeoEntityRenderer<TimeGearEntity> {

    public TimeGearRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new TimeGearModel());
        addRenderLayer(new AutoGlowingGeoLayer<>(this));
    }

    @Override
    public ResourceLocation getTextureLocation(TimeGearEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(Core.MODID, "textures/item/time_gear.png");
    }

    @Override
    public void render(TimeGearEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.scale(3f, 3f, 3f);
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }
}

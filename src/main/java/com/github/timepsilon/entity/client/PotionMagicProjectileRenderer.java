package com.github.timepsilon.entity.client;

import com.github.timepsilon.entity.custom.PotionMagicProjectile;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class PotionMagicProjectileRenderer extends EntityRenderer<PotionMagicProjectile> {

    public static final ModelLayerLocation MODEL_LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(IronsSpellbooks.MODID, "fireball_model"), "main");
    public static final ResourceLocation BASE_TEXTURE = IronsSpellbooks.id("textures/entity/fireball/magma.png");

    protected final ModelPart body;
    protected final ModelPart outline;

    public PotionMagicProjectileRenderer(EntityRendererProvider.Context context) {
        super(context);
        ModelPart modelpart = context.bakeLayer(MODEL_LAYER_LOCATION);
        this.body = modelpart.getChild("body");
        this.outline = modelpart.getChild("outline");
    }

    @Override
    public void render(PotionMagicProjectile entity, float yaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int light) {
        poseStack.pushPose();
        poseStack.translate(0, entity.getBoundingBox().getYsize() * .5f, 0);
        poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTick, entity.yRotO, entity.getYRot()) + 180));
        poseStack.mulPose(Axis.XP.rotationDegrees(Mth.lerp(partialTick, entity.xRotO, entity.getXRot())));
        VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(getTextureLocation(entity)));
        this.body.render(poseStack, consumer, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY);

        poseStack.scale(1.15f, 1.15f, 1.15f);
        this.outline.render(poseStack, consumer, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY);


        poseStack.popPose();

        super.render(entity, yaw, partialTick, poseStack, bufferSource, light);
    }

    @Override
    public ResourceLocation getTextureLocation(PotionMagicProjectile potionMagicProjectile) {
        return BASE_TEXTURE;
    }
}

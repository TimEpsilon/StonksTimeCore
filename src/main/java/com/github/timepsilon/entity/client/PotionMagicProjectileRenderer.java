package com.github.timepsilon.entity.client;

import com.github.timepsilon.Core;
import com.github.timepsilon.entity.custom.PotionMagicProjectile;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.entity.spells.acid_orb.AcidOrb;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import java.awt.*;

public class PotionMagicProjectileRenderer extends EntityRenderer<PotionMagicProjectile> {

    public static final ModelLayerLocation MODEL_LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(IronsSpellbooks.MODID, "acid_orb_model"), "main");
    private static final ResourceLocation ORB_TEXTURE = ResourceLocation.fromNamespaceAndPath(Core.MODID,"textures/entities/potion_magic_projectile.png");
    private static final ResourceLocation[] SWIRL_TEXTURES = {
            IronsSpellbooks.id("textures/entity/acid_orb/swirl_0.png"),
            IronsSpellbooks.id("textures/entity/acid_orb/swirl_1.png"),
            IronsSpellbooks.id("textures/entity/acid_orb/swirl_2.png"),
            IronsSpellbooks.id("textures/entity/acid_orb/swirl_3.png"),
            IronsSpellbooks.id("textures/entity/acid_orb/swirl_4.png"),
            IronsSpellbooks.id("textures/entity/acid_orb/swirl_5.png"),
            IronsSpellbooks.id("textures/entity/acid_orb/swirl_6.png"),
            IronsSpellbooks.id("textures/entity/acid_orb/swirl_7.png"),
            IronsSpellbooks.id("textures/entity/acid_orb/swirl_8.png"),
            IronsSpellbooks.id("textures/entity/acid_orb/swirl_9.png"),
            IronsSpellbooks.id("textures/entity/acid_orb/swirl_10.png")
    };

    protected final ModelPart orb;
    protected final ModelPart swirl;

    public PotionMagicProjectileRenderer(EntityRendererProvider.Context context) {
        super(context);
        ModelPart modelpart = context.bakeLayer(MODEL_LAYER_LOCATION);
        this.orb = modelpart.getChild("orb");
        this.swirl = modelpart.getChild("swirl");
    }

    @Override
    public void render(PotionMagicProjectile entity, float yaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int light) {
        poseStack.pushPose();
        poseStack.translate(0, entity.getBoundingBox().getYsize() * .5f, 0);

        PoseStack.Pose pose = poseStack.last();
        Matrix4f poseMatrix = pose.pose();
        Matrix3f normalMatrix = pose.normal();
        Vec3 motion = entity.getDeltaMovement();
        float xRot = -((float) (Mth.atan2(motion.horizontalDistance(), motion.y) * (double) (180F / (float) Math.PI)) - 90.0F);
        float yRot = -((float) (Mth.atan2(motion.z, motion.x) * (double) (180F / (float) Math.PI)) + 90.0F);
        poseStack.mulPose(Axis.YP.rotationDegrees(yRot));
        poseStack.mulPose(Axis.XP.rotationDegrees(xRot));
        VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(getTextureLocation(entity)));
        this.orb.render(poseStack, consumer, light, OverlayTexture.NO_OVERLAY, entity.getColor());

        float f = entity.tickCount + partialTicks;
        float swirlX = Mth.cos(.08f * f) * 180;
        float swirlY = Mth.sin(.08f * f) * 180;
        float swirlZ = Mth.cos(.08f * f + 5464) * 180;
        poseStack.mulPose(Axis.XP.rotationDegrees(swirlX));
        poseStack.mulPose(Axis.YP.rotationDegrees(swirlY));
        poseStack.mulPose(Axis.ZP.rotationDegrees(swirlZ));
        consumer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(getSwirlTextureLocation(entity)));
        poseStack.scale(1.15f, 1.15f, 1.15f);
        this.swirl.render(poseStack, consumer, light, OverlayTexture.NO_OVERLAY);


        poseStack.popPose();

        super.render(entity, yaw, partialTicks, poseStack, bufferSource, light);
    }

    @Override
    public ResourceLocation getTextureLocation(PotionMagicProjectile potionMagicProjectile) {
        return ORB_TEXTURE;
    }

    private ResourceLocation getSwirlTextureLocation(PotionMagicProjectile entity) {
        int frame = (entity.tickCount / 2) % SWIRL_TEXTURES.length;
        return SWIRL_TEXTURES[frame];
    }
}

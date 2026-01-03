package com.github.timepsilon.block.entity.client;

import com.github.timepsilon.block.entity.server.StonksTemporalChronoscopeEntity;
import com.github.timepsilon.create.STCPartialModels;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;

public class StonksTemporalChronoscopeRenderer extends KineticBlockEntityRenderer<StonksTemporalChronoscopeEntity> {

    protected final float ringIncrementAngle = (float) (2*Math.PI / 7 / 20);
    protected final float innerRingIncrementAngle = (float) (2*Math.PI / 13 / 20);
    protected final float timeGearIncrementAngle = (float) (-2*Math.PI / 5 / 20); // degree per second

    public StonksTemporalChronoscopeRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public boolean shouldRenderOffScreen(StonksTemporalChronoscopeEntity be) {
        return true;
    }

    @Override
    protected void renderSafe(StonksTemporalChronoscopeEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        if (VisualizationManager.supportsVisualization(be.getLevel())) return;

        VertexConsumer vb = buffer.getBuffer(RenderType.cutoutMipped());

        int lightColor = LevelRenderer.getLightColor(be.getLevel(), be.getBlockPos());

        SuperByteBuffer shaftHalf = CachedBuffers.partialFacing(AllPartialModels.SHAFT_HALF, be.getBlockState(), Direction.DOWN);
        SuperByteBuffer ring = CachedBuffers.partial(STCPartialModels.GYROSCOPE_OUTER_RING, be.getBlockState()).translate(0, 3/16f, 0);
        SuperByteBuffer innerRing = CachedBuffers.partial(STCPartialModels.GYROSCOPE_INNER_RING, be.getBlockState()).translate(0, 3/16f, 0);
        SuperByteBuffer timeGear = CachedBuffers.partial(STCPartialModels.GYROSCOPE_TIME_GEAR, be.getBlockState()).translate(0, 3/16f, 0);

        float time = AnimationTickHolder.getRenderTime(be.getLevel());

        standardKineticRotationTransform(shaftHalf, be, lightColor).renderInto(ms, vb);
        kineticRotationTransform(ring, be, Direction.Axis.Y, ringIncrementAngle * time, lightColor).renderInto(ms, vb);
        kineticRotationTransform(timeGear, be, Direction.Axis.Y, timeGearIncrementAngle * time, lightColor).renderInto(ms, vb);

        SuperByteBuffer ir = kineticRotationTransform(innerRing, be, Direction.Axis.Y, ringIncrementAngle * time, lightColor);
        ir.translate(0,14f/16,0);
        kineticRotationTransform(ir, be, Direction.Axis.X, innerRingIncrementAngle * time, lightColor);
        ir.translateBack(0,14f/16,0);
        ir.renderInto(ms, vb);
    }
}

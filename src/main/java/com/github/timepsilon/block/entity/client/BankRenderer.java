package com.github.timepsilon.block.entity.client;

import com.github.timepsilon.block.entity.server.BankBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.content.kinetics.millstone.MillstoneBlockEntity;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public class BankRenderer extends KineticBlockEntityRenderer<BankBlockEntity> {

    public BankRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public boolean shouldRenderOffScreen(BankBlockEntity be) {
        return true;
    }

    @Override
    protected SuperByteBuffer getRotatedModel(BankBlockEntity be, BlockState state) {
        return CachedBuffers.partial(AllPartialModels.SHAFT, state);
    }
}

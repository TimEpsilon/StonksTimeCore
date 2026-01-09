package com.github.timepsilon.client.renderer.layers;

import com.github.timepsilon.time.client.ClientOutState;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;

public class TransparentPlayerLayer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

    public TransparentPlayerLayer(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> renderer) {
        super(renderer);
    }

    // TODO : find why culling layers are also rendered transparent
    @Override
    public void render(PoseStack poseStack,
                       MultiBufferSource multiBufferSource,
                       int packedLight,
                       AbstractClientPlayer abstractClientPlayer,
                       float limbSwing,
                       float limbSwingAmount,
                       float partialTicks,
                       float ageInTicks,
                       float netHeadYaw,
                       float headPitch) {
        if (ClientOutState.canClientSeePlayer(abstractClientPlayer.getUUID())) {
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA,
                    GlStateManager.DestFactor.ONE_MINUS_CONSTANT_ALPHA);
            RenderSystem.setShaderColor(1f, 1f, 1f, 0.5f);
            RenderSystem.disableBlend();
        }
    }
}

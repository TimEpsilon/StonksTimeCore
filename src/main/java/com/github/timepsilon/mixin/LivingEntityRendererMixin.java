package com.github.timepsilon.mixin;

import com.github.timepsilon.config.STCConfigClient;
import com.github.timepsilon.time.client.ClientOutState;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;


/**
 * When a player is out, this makes them semi transparent.
 */
@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity, M extends EntityModel<T>> {

    @Inject(method="getRenderType", at=@At("HEAD"), cancellable=true)
    private void onGetRenderType(T entity, boolean bodyVisible, boolean translucent, boolean glowing, CallbackInfoReturnable<RenderType> info) {
        if (!(entity instanceof Player)) return;
        if (!STCConfigClient.CONFIG.SEE_OUT_TRANSLUCENT.getAsBoolean()) return;

        ResourceLocation resourcelocation = ((LivingEntityRenderer<T, M>)(Object)this).getTextureLocation(entity);
        if (ClientOutState.canClientSeePlayer(entity.getUUID())) {
            info.setReturnValue(RenderType.itemEntityTranslucentCull(resourcelocation));
        }
    }

    @ModifyArgs(
            method = "render(" +
                    "Lnet/minecraft/world/entity/LivingEntity;" +
                    "FFLcom/mojang/blaze3d/vertex/PoseStack;" +
                    "Lnet/minecraft/client/renderer/MultiBufferSource;" +
                    "I)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/model/EntityModel;" +
                            "renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;" +
                            "Lcom/mojang/blaze3d/vertex/VertexConsumer;" +
                            "III)V"
            )
    )
    private void overrideOverlayColor(Args args, @Local(argsOnly = true) LivingEntity entity) {
        if (!(entity instanceof Player)) return;
        if (!ClientOutState.canClientSeePlayer(entity.getUUID())) return;
        if (!STCConfigClient.CONFIG.SEE_OUT_TRANSLUCENT.getAsBoolean()) return;

        args.set(4,0x80FFFFFF);
    }

}

package com.github.timepsilon.events.handlers;

import com.github.timepsilon.Core;
import com.github.timepsilon.client.gui.overlay.TimerOverlay;
import com.github.timepsilon.create.STCPartialModels;
import com.github.timepsilon.entity.ModEntities;
import com.github.timepsilon.entity.client.TimeGearRenderer;
import com.github.timepsilon.entity.custom.TimeGearEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityAttachment;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RenderNameTagEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.common.util.TriState;
import org.joml.Matrix4f;

@EventBusSubscriber(value = Dist.CLIENT, modid = Core.MODID)
public class ClientEventsManager {

    @SubscribeEvent
    public static void clientInit(final FMLClientSetupEvent event) {
        STCPartialModels.init();
        Core.LOGGER.info("Registered partial models!");

        EntityRenderers.register(ModEntities.TIME_GEAR.get(), TimeGearRenderer::new);
    }

    @SubscribeEvent
    public static void onRegisterOverlay(RegisterGuiLayersEvent event) {
        event.registerAbove(VanillaGuiLayers.TITLE, ResourceLocation.fromNamespaceAndPath(Core.MODID,"timer_overlay"), TimerOverlay.instance);
    }

    /**
     * Handles the 2 lines above the Time Gear entity, showing both the owner name and the remaining time
     */
    @SubscribeEvent
    public static void onNameTagRender(RenderNameTagEvent event) {
        if (event.getEntity() instanceof TimeGearEntity entity) {
            event.setCanRender(TriState.FALSE);

            String owner = entity.getOwnerName();
            if (owner == null) return;

            renderAboveEntity(event, Component.literal(owner), 10);
            renderAboveEntity(event, entity.getDisplayName(), 2);
        }
    }

    private static void renderAboveEntity(RenderNameTagEvent event, Component text, int yOffset) {
        Vec3 vec3 = event.getEntity().getAttachments().getNullable(EntityAttachment.NAME_TAG, 0, event.getEntity().getViewYRot(event.getPartialTick()));
        event.getPoseStack().pushPose();
        event.getPoseStack().translate(vec3.x, vec3.y + (double)0.5F, vec3.z);
        event.getPoseStack().mulPose(Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation());
        event.getPoseStack().scale(0.025F, -0.025F, 0.025F);
        Matrix4f matrix4f = event.getPoseStack().last().pose();

        int x = event.getEntityRenderer().getFont().width(text)/2;

        event.getEntityRenderer().getFont().drawInBatch(
                text, -x, -yOffset, -1, true, matrix4f, event.getMultiBufferSource(), Font.DisplayMode.NORMAL, 0, event.getPackedLight()
        );
        event.getPoseStack().popPose();
    }

}

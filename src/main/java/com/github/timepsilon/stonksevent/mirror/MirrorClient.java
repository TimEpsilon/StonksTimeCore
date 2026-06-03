package com.github.timepsilon.stonksevent.mirror;

import com.github.timepsilon.Core;
import com.github.timepsilon.mobeffect.ModMobEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderFrameEvent;

@EventBusSubscriber(modid = Core.MODID, value = Dist.CLIENT)
public class MirrorClient {

    public static boolean IS_MIRROR_RUNNING = false;
    private static boolean canUnload = false;
    public static ResourceLocation MIRROR_SHADER = ResourceLocation.fromNamespaceAndPath(Core.MODID, "shaders/post/mirror.json");

    @SubscribeEvent
    public static void onRenderTick(RenderFrameEvent.Pre event) {
        Minecraft mc = Minecraft.getInstance();

        if (mc.player == null || mc.level == null) return;
        boolean isActive = mc.player.hasEffect(ModMobEffects.MIRROR);

        if (isActive) {
            loadShader(mc);
        } else {
            unloadShader(mc);
        }
    }

    private static void loadShader(Minecraft mc) {
        if (mc.gameRenderer.currentEffect() == null) {
            mc.gameRenderer.loadEffect(MIRROR_SHADER);
            canUnload =  true;
        } else if (!mc.gameRenderer.effectActive) {
            mc.gameRenderer.togglePostEffect();
        }
    }

    private static void unloadShader(Minecraft mc) {
        if (!canUnload) return;
        if (mc.gameRenderer.currentEffect() != null) {
            mc.gameRenderer.shutdownEffect();
            canUnload = false;
        }
    }

}

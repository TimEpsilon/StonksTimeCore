package com.github.timepsilon.stonksevent.speedup;

import com.github.timepsilon.Core;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

@EventBusSubscriber(modid = Core.MODID, value = Dist.CLIENT)
public class SpeedUpClient {

    public static boolean IS_SPEEDUP_RUNNING = false;
    private static boolean canUnload = false;
    public static ResourceLocation SPEED_UP_SHADER = ResourceLocation.fromNamespaceAndPath(Core.MODID, "shaders/post/speed_up.json");


    @SubscribeEvent
    public static void onTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        mc.execute(() -> {
            if (IS_SPEEDUP_RUNNING) {
                loadShader(mc);
            } else {
                unloadShader(mc);
            }
        });
    }

    private static void loadShader(Minecraft mc) {
        if (mc.gameRenderer.currentEffect() == null) {
            mc.gameRenderer.loadEffect(
                    SPEED_UP_SHADER
            );
            canUnload =  true;
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

package com.github.timepsilon.shader;


import com.github.timepsilon.Core;
import com.github.timepsilon.gui.overlay.TimerOverlay;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

@EventBusSubscriber(value = Dist.CLIENT, modid = Core.MODID)
public class ShaderUtil {

    public static boolean toRenderShader = true;

    private static final ResourceLocation DESATURATE_POST = ResourceLocation.fromNamespaceAndPath(Core.MODID, "shaders/post/desaturate.json");

    public ShaderUtil(IEventBus eventBus) {
    }


    @SubscribeEvent
    public static void onRender(ClientTickEvent.Post event) {
        //if (!toRenderShader) return;
        //Minecraft mc = Minecraft.getInstance();
        //System.out.println("Update Shader");

        //if (TimerOverlay.instance.isOut()) {
        //    System.out.println("Desaturate");
        //    mc.gameRenderer.loadEffect(DESATURATE_POST);
        //} else {
        //    System.out.println("Basic");
        //    mc.gameRenderer.shutdownEffect();
        //}
        //toRenderShader = false;
    }
}

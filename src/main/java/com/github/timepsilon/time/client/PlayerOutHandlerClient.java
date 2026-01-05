package com.github.timepsilon.time.client;

import com.github.timepsilon.Core;
import com.github.timepsilon.client.renderer.layers.TransparentPlayerLayer;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RenderPlayerEvent;

@EventBusSubscriber(value = Dist.CLIENT, modid = Core.MODID)
public class PlayerOutHandlerClient {

    // Layer needs to be applied once, the logic is then contained within the object
    private static boolean hasLayerFlag = false;

    public PlayerOutHandlerClient() {}
    
    /**
     * When a player is out, this makes them semi transparent.
     */
    @SubscribeEvent
    public static void playerRender(RenderPlayerEvent.Post event) {
        if (Minecraft.getInstance().player == null) return;

        if (!hasLayerFlag) {
            event.getRenderer().addLayer(new TransparentPlayerLayer(event.getRenderer()));
        }
    }
    
}

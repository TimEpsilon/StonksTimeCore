package com.github.timepsilon.time;

import com.github.timepsilon.Core;
import com.github.timepsilon.packets.server.isOutPacket;
import com.github.timepsilon.time.client.ClientOutState;
import dev.ithundxr.createnumismatics.Numismatics;
import dev.ithundxr.createnumismatics.content.backend.BankAccount;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.util.UUID;

@Mod(value = Core.MODID)
public class PlayerOutHandler {

    public static ResourceLocation DESATURATE_SHADER = ResourceLocation.fromNamespaceAndPath(Core.MODID, "shaders/post/desaturate.json");

    public PlayerOutHandler() {}

    /** Set a player's out status
     * <p>
     *
     * Offline player logic
     *
     * @param uuid : the player's uuid, can be either of an offline or an online player
     */
    public static void setOut(UUID uuid, boolean out) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server != null) {
            PlayerOutData timer = PlayerOutData.getPlayerTimer(server);
            timer.setOut(uuid, out);
        } else {
            Core.LOGGER.error("Player status couldn't be set to out : server is null");
        }
    }

    /** Set a player's out status
     * <p>
     *
     * Online player logic
     *
     * @param player : an online server player
     */
    public static void setOut(ServerPlayer player, boolean out) {
        setOut(player.getUUID(), out);
        System.out.println(out);

        if (out) {
            // outPlayer desaturated visual
            CatnipServices.NETWORK.sendToClient(player, new isOutPacket(true));

            // outPlayer semi-transparent

            // Global chat message

            // Global sound


        } else {
            CatnipServices.NETWORK.sendToClient(player, new isOutPacket(false));
        }
    }

    /** Manages the permanent effects of being out (only needed when player is online)
     * <p> i.e. the desaturated effect, transparent player model
     *
     * @param player : the ServerPlayer that is out
     */
    public static void permanentIsOutEffects(ServerPlayer player) {

    }

    /**
     * When a player is out, this tries to apply the desaturate shader every tick
     * <p> However, in order to not block other post shaders effect,
     * when a player isn't out, the shader is only removed once </p>
     */
    @SubscribeEvent
    public void onClientTick(ClientTickEvent.Post event) {
        if (Minecraft.getInstance().player == null) return;
        if (Minecraft.getInstance().level == null) return;

        GameRenderer renderer = Minecraft.getInstance().gameRenderer;

        if (ClientOutState.IS_OUT) {
            if (renderer.currentEffect() == null) {
                renderer.loadEffect(DESATURATE_SHADER);
            }
        } else {
            if  (renderer.currentEffect() == null) return;
            // If the player is not out but has the desaturate effect (right after being set to not out)
            // Remove the effect
            if (renderer.currentEffect().getName().equals(DESATURATE_SHADER.toString())) {
                renderer.shutdownEffect();
            }
        }
    }

    @SubscribeEvent
    public void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        MinecraftServer level = player.server;
        PlayerOutData timer = PlayerOutData.getPlayerTimer(level);

        // If the player is out, send them the desaturate packet, else, removes it
        PlayerOutHandler.setOut(player, timer.isOut(player.getUUID()));
    }

    @SubscribeEvent
    public void onPlayerLeave(PlayerEvent.PlayerLoggedOutEvent event) {
        // Remove any kind of post effect to the player when they leave

        if (!(event.getEntity() instanceof LocalPlayer player)) return;
        Minecraft.getInstance().gameRenderer.shutdownEffect();

    }

}

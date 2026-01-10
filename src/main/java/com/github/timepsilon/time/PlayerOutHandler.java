package com.github.timepsilon.time;

import com.github.timepsilon.Core;
import com.github.timepsilon.packets.server.IsOutPacket;
import com.github.timepsilon.packets.server.PlayersAreOutPacket;
import com.github.timepsilon.sounds.ModSounds;
import com.github.timepsilon.time.client.ClientOutState;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.common.UsernameCache;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.awt.*;
import java.util.Set;
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
            PlayerOutData timer = PlayerOutData.getPlayerOutData(server);
            timer.setOut(uuid, out);
            String username = UsernameCache.getLastKnownUsername(uuid);

            if (out) {
                Core.LOGGER.info("{} is out of time", username);

                for (ServerPlayer p : server.getPlayerList().getPlayers()) {
                    // Global chat message
                    p.sendSystemMessage(
                            Component.translatable("info.stonkstimecore.player_is_out", username)
                                    .withColor(Color.RED.getRGB()));
                }

                // Global sound
                server.overworld().playSound(null, 0,0,0, SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.PLAYERS, 10000.0f, 0.6f);
                server.overworld().playSound(null, 0,0,0, ModSounds.TIME_OUT.get(), SoundSource.PLAYERS, 10000.0f, 0.8f);
            }




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

        // Updates the "semi-transparent" list to every player
        Set<UUID> playerOutSet = PlayersAreOutPacket.getOutPlayers(player.server);
        for (ServerPlayer p : player.server.getPlayerList().getPlayers()) {
            CatnipServices.NETWORK.sendToClient(p, new PlayersAreOutPacket(playerOutSet));
        }

        if (out) {
            // outPlayer desaturated visual
            CatnipServices.NETWORK.sendToClient(player, new IsOutPacket(true));

            // Player chat message
            player.sendSystemMessage(Component.translatable("info.stonkstimecore.self_is_out", player.getName()).withColor(Color.GRAY.getRGB()).withStyle(ChatFormatting.ITALIC));

        } else {
            CatnipServices.NETWORK.sendToClient(player, new IsOutPacket(false));
        }
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
        PlayerOutData timer = PlayerOutData.getPlayerOutData(level);

        // If the player is out, send them the desaturate packet, else, removes it
        CatnipServices.NETWORK.sendToClient(player, new IsOutPacket(timer.isOut(player.getUUID())));

        // Send the list of out players to a joining player
        Set<UUID> playerOutSet = PlayersAreOutPacket.getOutPlayers(player.server);
        CatnipServices.NETWORK.sendToClient(player, new PlayersAreOutPacket(playerOutSet));
    }

    @SubscribeEvent
    public void onPlayerLeave(PlayerEvent.PlayerLoggedOutEvent event) {
        // Remove any kind of post effect to the player when they leave
        if (!(event.getEntity().isLocalPlayer())) return;
        Minecraft.getInstance().gameRenderer.shutdownEffect();

    }

}

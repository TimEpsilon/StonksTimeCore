package com.github.timepsilon.time;

import com.github.timepsilon.Core;
import com.github.timepsilon.packets.server.TimerSyncPacket;
import com.github.timepsilon.utils.TimeUtils;
import dev.ithundxr.createnumismatics.Numismatics;
import dev.ithundxr.createnumismatics.content.backend.BankAccount;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.UUID;

@Mod(value = Core.MODID, dist = Dist.DEDICATED_SERVER)
public class TimerHandler {

    int seconds;

    /**
     * Executed every second (if player not out):
     * <ul>
     *   <li>The player's account is charged the MCoins equivalent of 1 second.</li>
     *   <li>The money is converted into time.</li>
     *   <li>If time is 0 -> set player as out</li>
     * </ul>
     */
    @SubscribeEvent
    public void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (!(player.serverLevel().getGameTime() % 20 == 0)) return;

        UUID uuid = player.getUUID();
        MinecraftServer level = player.server;
        PlayerOutData timer = PlayerOutData.getPlayerOutData(level);
        BankAccount account = Numismatics.BANK.getAccount(uuid);

        if (timer.isOut(uuid)) {
            sendOverlayPacket(player, 0, 0, true);
            return; // Time stops for out people
        }

        // Decrease account by 1s
        account.deduct(TimeUtils.TIME_TO_MONEY, true);

        // Convert money to seconds
        seconds = account.getBalance() / TimeUtils.TIME_TO_MONEY;

        // If time is now 0 -> set as out, preventing timer to update
        if (account.getBalance() <= 0) {
            PlayerOutHandler.setOut(player, true);
        }

        sendOverlayPacket(player, seconds, account.getBalance(), timer.isOut(uuid));
    }

    /**
     * Logic for giving a player time / money on first join
     */
    @SubscribeEvent
    public void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        BankAccount account = Numismatics.BANK.getOrCreateAccount(player.getUUID(), BankAccount.Type.PLAYER);
        MinecraftServer level = player.server;
        PlayerOutData timer = PlayerOutData.getPlayerOutData(level);

        // Setup for players with no money
        // If the player is not in the map, we assume that it's their first time connecting
        if (!timer.getPlayerIsOut().containsKey(player.getUUID())) {
            Core.LOGGER.info("{} joined for the first time. {}s have been added to their timer", player.getName(), TimeUtils.BASE_TIME);
            account.setBalance(TimeUtils.BASE_TIME * TimeUtils.TIME_TO_MONEY);
            PlayerOutHandler.setOut(player, false);
        }
    }

    public static void sendOverlayPacket(ServerPlayer player, int time, int money,  boolean isOut) {
        CatnipServices.NETWORK.sendToClient(player, new TimerSyncPacket(time, money, isOut));
    }
}

package com.github.timepsilon.time;

import com.github.timepsilon.Core;
import com.github.timepsilon.events.BankAccountUpdateEvent;
import com.github.timepsilon.gui.packets.TimerSyncPacket;
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
        PlayerOutData timer = PlayerOutData.getPlayerTimer(level);
        BankAccount account = Numismatics.BANK.getAccount(uuid);

        if (timer.isOut(uuid)) {
            sendOverlayPacket(player, 0, 0, true);
            return; // Time stops for out people
        }

        // Decrease account by 1s
        account.deduct(TimeManager.TIME_TO_MONEY, true);

        // Convert money to seconds
        seconds = account.getBalance() / TimeManager.TIME_TO_MONEY;

        // If time is now 0 -> set as out, preventing timer to update
        if (account.getBalance() <= 0) {
            timer.setOut(uuid, true);
            // TODO : announce in chat and to player, make player translucent
        }

        System.out.println(TimeManager.secondsToTime(seconds));
    }

    /**
     * Logic for giving a player time / money on first join
     */
    @SubscribeEvent
    public void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        BankAccount account = Numismatics.BANK.getAccount(player.getUUID());
        MinecraftServer level = player.server;
        PlayerOutData timer = PlayerOutData.getPlayerTimer(level);

        // Setup for players with no money
        // If the player is not in the map, we assume that it's their first time connecting
        if (!timer.getPlayerIsOut().containsKey(player.getUUID())) {
            System.out.println("First Connection");
            account.setBalance(TimeManager.BASE_TIME * TimeManager.TIME_TO_MONEY);
            timer.setOut(player.getUUID(), false);
        }
    }

    /**
     * Since this is called when removing 1s worth of money AND when a simple transaction is done,
     * The GUI is updated through here
     * <p>
     * Executed every transaction :
     * <ul>
     *   <li>The money is converted to seconds.</li>
     *   <li>The GUI is updated.</li>
     * </ul>
     */
    @SubscribeEvent
    public void onBankUpdate(BankAccountUpdateEvent event) {
        BankAccount account = event.getBank();
        UUID uuid = account.id;
        PlayerOutData timer = PlayerOutData.getPlayerTimer(event.getServer());
        // player will be null if disconnected (through shops)
        ServerPlayer player = event.getServer().getPlayerList().getPlayer(uuid);

        // Convert money to seconds
        seconds = account.getBalance() / TimeManager.TIME_TO_MONEY;

        // Packet for overlay
        int money = event.getNewBalance();

        // Update GUI
        if (player != null) {
            sendOverlayPacket(player, seconds, money, timer.isOut(uuid));
        }
    }

    public static void sendOverlayPacket(ServerPlayer player, int time, int money,  boolean isOut) {
        CatnipServices.NETWORK.sendToClient(player, new TimerSyncPacket(time, money, isOut));
    }
}

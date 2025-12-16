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
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.UUID;

@Mod(value = Core.MODID, dist = Dist.DEDICATED_SERVER)
public class TickHook {

    /**
     * Logic:
     * <p>
     * Executed every second (if player not out):
     * <ul>
     *   <li>The timer is set to the player's money (money &gt; time logic).</li>
     *   <li>The timer is decreased by one.</li>
     *   <li>The player's account is charged the MCoins equivalent of 1 second.</li>
     *   <li>If time is 0 -> set player as out</li>
     * </ul>
     */

    @SubscribeEvent
    public void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (!(player.serverLevel().getGameTime() % 20 == 0)) return;

        UUID uuid = player.getUUID();
        MinecraftServer level = player.server;
        PlayerTimers timer = PlayerTimers.getPlayerTimer(level);
        BankAccount account = Numismatics.BANK.getAccount(uuid);

        System.out.println(timer.isOut(uuid) + " " + timer.get(uuid) + " " + account.getBalance());

        if (timer.isOut(uuid)) return;

        // Setup for players with no timers
        if (!timer.PlayerTimerMap.containsKey(uuid)) {
            timer.set(uuid, PlayerTimers.BASE_TIME);
            account.setBalance(PlayerTimers.BASE_TIME * PlayerTimers.TIME_TO_MONEY);
            return;
        }

        // Set timer to money equivalent
        timer.set(uuid, account.getBalance()/PlayerTimers.TIME_TO_MONEY);

        // Decrease timer and account by 1s
        timer.add(uuid, -1);
        account.deduct(PlayerTimers.TIME_TO_MONEY, true);

        // If time is now 0 -> set as out, preventing timer to update
        int seconds = timer.get(uuid);
        if (seconds <= 0) {
            timer.setOut(uuid, true);
            // TODO : announce in chat and to player, make player translucent
        }

        // Packet for overlay
        int money = (account == null) ? 0 : account.getBalance();
        CatnipServices.NETWORK.sendToClient(player, new TimerSyncPacket(seconds, money));
    }

    @SubscribeEvent
    public void onBankUpdate(BankAccountUpdateEvent event) {
        BankAccount bank = event.getBank();
        UUID uuid = bank.id;

    }
}

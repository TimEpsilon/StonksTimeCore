package com.github.timepsilon.time;

import com.github.timepsilon.Core;
import com.github.timepsilon.gui.packets.StonksTemporalChronoscopeMoneyPacket;
import com.github.timepsilon.gui.packets.TimerSyncPacket;
import dev.ithundxr.createnumismatics.Numismatics;
import dev.ithundxr.createnumismatics.content.backend.BankAccount;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.UUID;

@Mod(value = Core.MODID, dist = Dist.DEDICATED_SERVER)
public class TickHook {

    @SubscribeEvent
    public void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (!(player.serverLevel().getGameTime() % 20 == 0)) return;

        UUID uuid = player.getUUID();
        MinecraftServer level = player.server;
        PlayerTimer timer = PlayerTimer.getPlayerTimer(level);
        BankAccount account = Numismatics.BANK.getAccount(uuid);

        if (!timer.PlayerTimerMap.containsKey(uuid)) {
            timer.set(uuid, PlayerTimer.BASE_TIME);
            return;
        }

        timer.add(uuid, -1);

        int money;
        if (account == null) {money = 0;}
        else {money = account.getBalance();}

        CatnipServices.NETWORK.sendToClient(player, new TimerSyncPacket(timer.get(uuid), money));
    }
}

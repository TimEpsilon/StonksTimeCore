package com.github.timepsilon.time;

import com.github.timepsilon.Core;
import com.github.timepsilon.packets.server.TimerSyncPacket;
import com.github.timepsilon.utils.TimeUtils;
import dev.ithundxr.createnumismatics.Numismatics;
import dev.ithundxr.createnumismatics.content.backend.BankAccount;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.UUID;

@EventBusSubscriber(modid = Core.MODID)
public class TimerHandler {

    public static final ResourceLocation TIME_HP = ResourceLocation.fromNamespaceAndPath(Core.MODID, "time_hp");

    /**
     * Executed every second (if player not out):
     * <ul>
     *   <li>The player's account is charged the MCoins equivalent of 1 second.</li>
     *   <li>The money is converted into time.</li>
     *   <li>If time is 0 -> set player as out</li>
     * </ul>
     */
    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (!(player.serverLevel().getGameTime() % 20 == 0)) return;

        UUID uuid = player.getUUID();
        MinecraftServer level = player.server;
        PlayerOutData timer = PlayerOutData.getPlayerOutData(level);
        BankAccount account = Numismatics.BANK.getOrCreateAccount(uuid, BankAccount.Type.PLAYER);

        if (timer.isOut(uuid)) {
            sendOverlayPacket(player, 0, 0, true);
            return; // Time stops for out people
        }

        // Decrease account by 1s
        account.deduct(TimeUtils.TIME_TO_MONEY, true);

        // Convert money to seconds
        int seconds = account.getBalance() / TimeUtils.TIME_TO_MONEY;

        // If time is now 0 -> set as out, preventing timer to update
        if (account.getBalance() <= 0) {
            PlayerOutHandler.setOut(player, true);
        }

        // Update Overlay
        sendOverlayPacket(player, seconds, account.getBalance(), timer.isOut(uuid));


        // HP logic
        if (!(TimeUtils.DANGER_TIME < seconds && seconds < TimeUtils.SAFE_TIME)) {
            int hp = TimeUtils.howMuchHP(seconds);
            if (!player.getAttribute(Attributes.MAX_HEALTH).hasModifier(TIME_HP)) {
                AttributeModifier timeHPModifier = new AttributeModifier(TIME_HP, 0, AttributeModifier.Operation.ADD_VALUE);
                player.getAttribute(Attributes.MAX_HEALTH).addPermanentModifier(timeHPModifier);
            }

            if ((hp != player.getAttribute(Attributes.MAX_HEALTH).getModifier(TIME_HP).amount())) {
                AttributeModifier timeHPModifier = new AttributeModifier(TIME_HP, hp, AttributeModifier.Operation.ADD_VALUE);
                player.getAttribute(Attributes.MAX_HEALTH).addOrReplacePermanentModifier(timeHPModifier);
            }
        }
    }

    /**
     * Logic for giving a player time / money on first join
     */
    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        BankAccount account = Numismatics.BANK.getOrCreateAccount(player.getUUID(), BankAccount.Type.PLAYER);
        MinecraftServer level = player.server;
        PlayerOutData timer = PlayerOutData.getPlayerOutData(level);

        // Setup for HP
        if (!player.getAttribute(Attributes.MAX_HEALTH).hasModifier(TIME_HP)) {
            AttributeModifier timeHPModifier = new AttributeModifier(TIME_HP, 0, AttributeModifier.Operation.ADD_VALUE);
            player.getAttribute(Attributes.MAX_HEALTH).addPermanentModifier(timeHPModifier);
        }

        // Setup for players with no money
        // If the player is not in the map, we assume that it's their first time connecting
        if (!timer.getPlayerIsOut().containsKey(player.getUUID())) {
            Core.LOGGER.info("{} joined for the first time. {}s have been added to their timer", player.getName(), TimeUtils.BASE_TIME);
            account.setBalance(TimeUtils.BASE_TIME * TimeUtils.TIME_TO_MONEY);
            PlayerOutHandler.setOut(player, false);
        }
    }

    /**
    * Penalize death by losing n% of total money
     **/
    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        PlayerOutData timer = PlayerOutData.getPlayerOutData(player.getServer());
        boolean isOut = timer.isOut(player.getUUID());
        if (isOut) return;

        BankAccount account = Numismatics.BANK.getOrCreateAccount(player.getUUID(), BankAccount.Type.PLAYER);
        account.deduct((int) (account.getBalance()*TimeUtils.DEATH_LOSS));
    }

    public static void sendOverlayPacket(ServerPlayer player, int time, int money,  boolean isOut) {
        CatnipServices.NETWORK.sendToClient(player, new TimerSyncPacket(time, money, isOut));
    }
}

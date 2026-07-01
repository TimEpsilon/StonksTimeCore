package com.github.timepsilon.time;

import com.github.timepsilon.Core;
import com.github.timepsilon.config.STCConfigServer;
import com.github.timepsilon.config.SqlStatsGate;
import com.github.timepsilon.database.MoneyDatabase;
import com.github.timepsilon.packets.server.TimerInfoPacket;
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

import static com.github.timepsilon.time.PlayerOutHandler.setLowHp;

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
            setLowHp(player);
            return; // Time stops for out people
        }

        // Decrease account by 1s
        account.deduct(STCConfigServer.CONFIG.TIME_TO_MONEY.getAsInt(), true);

        // Convert money to seconds
        int seconds = account.getBalance() / STCConfigServer.CONFIG.TIME_TO_MONEY.getAsInt();

        // Update Overlay
        sendOverlayPacket(player, seconds, account.getBalance(), timer.isOut(uuid));

        // HP logic
        if (!(STCConfigServer.CONFIG.DANGER_TIME.getAsInt() < seconds && seconds < STCConfigServer.CONFIG.SAFE_TIME.getAsInt())) {
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

        // Hurt when no time
        if (account.getBalance() <= 0) player.hurt(player.damageSources().genericKill(), 1);
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
            Core.LOGGER.info("{} joined for the first time. {}s have been added to their timer", player.getName(), STCConfigServer.CONFIG.BASE_TIME.getAsInt());
            account.setBalance(STCConfigServer.CONFIG.BASE_TIME.getAsInt() * STCConfigServer.CONFIG.TIME_TO_MONEY.getAsInt());
            PlayerOutHandler.setOut(player, false);
        }
    }

    /**
    * Penalize death by losing n% of total money
     **/
    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        BankAccount account = Numismatics.BANK.getOrCreateAccount(player.getUUID(), BankAccount.Type.PLAYER);

        // If time is now 0 -> set as out, preventing timer to update
        if (account.getBalance() <= 0) {
            PlayerOutHandler.setOut(player, true);
        }

        // Nothing happens if player is out (no money to lose)
        PlayerOutData timer = PlayerOutData.getPlayerOutData(player.getServer());
        boolean isOut = timer.isOut(player.getUUID());
        if (isOut) return;

        // Lose 10% of player money (leaving a minimum of 10 remaining seconds)
        TimeUtils.loseAndExplodeOnDeath(account, player, event.getSource().getWeaponItem());
    }

    @SubscribeEvent
    public static void onPlayerLeave(PlayerEvent.PlayerLoggedOutEvent event) {
        if (SqlStatsGate.isEnabled()) {
            MoneyDatabase.getDatabase().saveBanks();
        }
    }

    public static void sendOverlayPacket(ServerPlayer player, int time, int money,  boolean isOut) {
        CatnipServices.NETWORK.sendToClient(player, new TimerSyncPacket(time, money, isOut));
    }

    public static void sendInfoPacket(ServerPlayer player, String message) {
        CatnipServices.NETWORK.sendToClient(player, new TimerInfoPacket(message));
    }
}

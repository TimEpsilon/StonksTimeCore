package com.github.timepsilon.stonksevent.winmoney;

import com.github.timepsilon.config.STCConfigServer;
import com.github.timepsilon.stonksevent.AbstractRandomStonksEvent;
import com.github.timepsilon.time.PlayerOutData;
import com.github.timepsilon.time.TimerHandler;
import com.github.timepsilon.utils.TimeUtils;
import dev.ithundxr.createnumismatics.Numismatics;
import dev.ithundxr.createnumismatics.content.backend.BankAccount;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Random;

import static com.github.timepsilon.utils.TimeUtils.givePlayer;

public class SREWinMoney extends AbstractRandomStonksEvent {

    private static final Random RANDOM = new Random();

    public SREWinMoney(float weight, boolean isPositive, String combination, String name) {
        super(weight, isPositive, combination, name);
    }

    @Override
    public void onStart(Player player) {
        int amount = (int) Math.max(0,RANDOM.nextGaussian(getMean(), getStd())); // We always gain > 0s
        BankAccount account = Numismatics.BANK.getOrCreateAccount(player.getUUID(), BankAccount.Type.PLAYER);

        PlayerOutData timer = PlayerOutData.getPlayerOutData(player.getServer());
        boolean isOut = timer.isOut(player.getUUID());

        int money = amount  * STCConfigServer.CONFIG.TIME_TO_MONEY.getAsInt();
        if (isOut) {
            List<ItemStack> coins = TimeUtils.minimumNumberOfCoins(money);

            // Identical to a /give animation
            for (ItemStack item : coins) {
                givePlayer(player, item);
            }
        } else {
            account.deposit(money);
            TimerHandler.sendInfoPacket((ServerPlayer) player, "+"+money+"\u9000");
        }

        player.sendSystemMessage(
                Component.translatable("sre.stonkstimecore.win_money.amount", TimeUtils.secondsToTime(amount), money)
                        .withStyle(ChatFormatting.GREEN)
        );
    }

    @Override
    public void onStop(Player player) {

    }

    public static double getMean() {
        return STCConfigServer.CONFIG.SRE_GAIN_AMOUNT.get();
    }

    public static double getStd() {
        return STCConfigServer.CONFIG.SRE_GAIN_ERROR.get();
    }
}

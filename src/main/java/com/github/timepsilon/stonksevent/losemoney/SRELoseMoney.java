package com.github.timepsilon.stonksevent.losemoney;

import com.github.timepsilon.config.STCConfigServer;
import com.github.timepsilon.stonksevent.AbstractRandomStonksEvent;
import com.github.timepsilon.time.PlayerOutData;
import com.github.timepsilon.utils.TimeUtils;
import dev.ithundxr.createnumismatics.Numismatics;
import dev.ithundxr.createnumismatics.content.backend.BankAccount;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import java.util.Random;

public class SRELoseMoney extends AbstractRandomStonksEvent {

    private final static double MEAN = STCConfigServer.CONFIG.SRE_LOSS_AMOUNT.get(); // in seconds
    private final static double STD = STCConfigServer.CONFIG.SRE_LOSS_ERROR.get();
    private static final Random RANDOM = new Random();

    public SRELoseMoney(float weight, boolean isPositive, String combination, String name) {
        super(weight, isPositive, combination, name);
    }

    @Override
    public void onStart(Player player) {

        BankAccount account = Numismatics.BANK.getOrCreateAccount(player.getUUID(), BankAccount.Type.PLAYER);
        int amount = account.getBalance() > 600
                    ? (int) Math.clamp(RANDOM.nextGaussian(MEAN, STD), 0, account.getBalance() - 600)
                    : 0; // We always lose (playerBalance - 600s > x > 0s)

        PlayerOutData timer = PlayerOutData.getPlayerOutData(player.getServer());
        boolean isOut = timer.isOut(player.getUUID());

        if (isOut) {
            player.kill();
            player.sendSystemMessage(
                    Component.translatable("sre.stonkstimecore.lose_money.isout")
                            .withStyle(ChatFormatting.RED)
            );
        } else {
            account.deduct(amount * TimeUtils.TIME_TO_MONEY);

            player.sendSystemMessage(
                    Component.translatable("sre.stonkstimecore.lose_money.amount", TimeUtils.secondsToTime(amount), amount * TimeUtils.TIME_TO_MONEY)
                            .withStyle(ChatFormatting.RED)
            );
        }
    }

    @Override
    public void onStop(Player player) {

    }
}

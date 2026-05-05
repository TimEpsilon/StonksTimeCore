package com.github.timepsilon.stonksevent.winmoney;

import com.github.timepsilon.config.STCConfigServer;
import com.github.timepsilon.stonksevent.AbstractRandomStonksEvent;
import com.github.timepsilon.time.PlayerOutData;
import com.github.timepsilon.utils.TimeUtils;
import dev.ithundxr.createnumismatics.Numismatics;
import dev.ithundxr.createnumismatics.content.backend.BankAccount;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Random;

public class SREWinMoney extends AbstractRandomStonksEvent {

    private final static double MEAN = STCConfigServer.CONFIG.SRE_GAIN_AMOUNT.get(); // in seconds
    private final static double STD = STCConfigServer.CONFIG.SRE_GAIN_ERROR.get();
    private static final Random RANDOM = new Random();

    public SREWinMoney(float weight, boolean isPositive, String combination, String name) {
        super(weight, isPositive, combination, name);
    }

    @Override
    public void onStart(Player player) {
        int amount = (int) Math.max(0,RANDOM.nextGaussian(MEAN, STD)); // We always gain > 0s
        BankAccount account = Numismatics.BANK.getOrCreateAccount(player.getUUID(), BankAccount.Type.PLAYER);

        PlayerOutData timer = PlayerOutData.getPlayerOutData(player.getServer());
        boolean isOut = timer.isOut(player.getUUID());


        if (isOut) {
            List<ItemStack> coins = TimeUtils.minimumNumberOfCoins(amount);

            // Identical to a /give animation
            for (ItemStack item : coins) {
                givePlayer(player, item);
            }
        } else {
            account.deposit(amount * TimeUtils.TIME_TO_MONEY);
        }

        player.sendSystemMessage(
                Component.translatable("sre.stonkstimecore.win_money.amount", TimeUtils.secondsToTime(amount), amount * TimeUtils.TIME_TO_MONEY)
                        .withStyle(ChatFormatting.GREEN)
        );
    }

    @Override
    public void onStop(Player player) {

    }

    public static void givePlayer(Player player, ItemStack item) {
        boolean didGet = player.getInventory().add(item);
        if (didGet && item.isEmpty()) {
            ItemEntity itementity = player.drop(item, false);
            if (itementity != null) {
                itementity.makeFakeItem();
            }

            player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2F, ((player.getRandom().nextFloat() - player.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F);
            player.containerMenu.broadcastChanges();
        } else {
            ItemEntity itementity = player.drop(item, false);
            if (itementity != null) {
                itementity.setNoPickUpDelay();
                itementity.setTarget(player.getUUID());
            }
        }
    }
}

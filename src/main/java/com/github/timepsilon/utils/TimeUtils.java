package com.github.timepsilon.utils;

import com.github.timepsilon.config.STCConfigServer;
import dev.ithundxr.createnumismatics.content.backend.Coin;
import net.createmod.catnip.data.Couple;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TimeUtils {

    public static final int BASE_TIME = STCConfigServer.CONFIG.BASE_TIME.getAsInt(); // 4h
    public static final int TIME_TO_MONEY = STCConfigServer.CONFIG.TIME_TO_MONEY.getAsInt();
    public static final int SAFE_TIME = STCConfigServer.CONFIG.SAFE_TIME.getAsInt(); // 6h - above this time, slowly start gaining HP
    public static final int DANGER_TIME = STCConfigServer.CONFIG.DANGER_TIME.getAsInt(); // 30min - below this time, start loosing HP
    public static final int DT_FOR_GAIN_1HP = STCConfigServer.CONFIG.DT_FOR_GAIN_1HP.getAsInt(); // 2h - time to gain 1 additional HP above SAFE_TIME
    public static final int DT_FOR_LOSE_1HP = STCConfigServer.CONFIG.DT_FOR_LOSE_1HP.getAsInt(); // 3min - time to lose 1 HP below DANGER_TIME
    public static final int MAX_HP = STCConfigServer.CONFIG.MAX_HP.getAsInt();
    public static final int MIN_HP = STCConfigServer.CONFIG.MIN_HP.getAsInt();
    public static final double DEATH_LOSS = STCConfigServer.CONFIG.DEATH_LOSS.getAsDouble();

    public static String secondsToTime(int seconds) {
        int h = seconds / 3600;
        int m = (seconds % 3600) / 60;
        int s = seconds % 60;
        return String.format("%d:%02d:%02d", h, m, s);
    }

    public static String SCTToTime(double sct) {
        int seconds = (int) Math.ceil(sct / TIME_TO_MONEY);
        int h = seconds / 3600;
        int m = (seconds % 3600) / 60;
        int s = seconds % 60;

        if (h > 0) {
            return String.format("%dh %02dm %02ds", h, m, s);
        } else if (m > 0) {
            return String.format("%dm %02ds", m, s);
        } else {
            return String.format("%ds", s);
        }
    }

    public static int howMuchHP(int seconds) {
        int hp = 0;
        if (seconds < DANGER_TIME) {
            hp = Math.max(-MIN_HP, (seconds-DANGER_TIME)/DT_FOR_LOSE_1HP - 1);
        } else if (seconds > SAFE_TIME) {
            hp = Math.min(MAX_HP, (seconds-SAFE_TIME)/DT_FOR_GAIN_1HP + 1);
        }
        return hp;
    }

    public static List<ItemStack> minimumNumberOfCoins(int amount) {
        List<ItemStack> coins = new ArrayList<>();
        for (Coin coin : Arrays.stream(Coin.values()).toList().reversed()) {
            Couple<Integer> coinAmount = coin.convert(amount);
            amount = coinAmount.getSecond();
            if (coinAmount.getFirst() > 0) coins.add(coin.asStack(coinAmount.getFirst()));
        }

        System.out.println(coins);
        return coins;
    }

}

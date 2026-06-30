package com.github.timepsilon.utils;

import com.github.timepsilon.config.STCConfigServer;
import dev.ithundxr.createnumismatics.content.backend.BankAccount;
import dev.ithundxr.createnumismatics.content.backend.Coin;
import net.createmod.catnip.data.Couple;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;

import java.nio.ByteBuffer;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class TimeUtils {

    public static String secondsToTime(int seconds) {
        return TimeCore.secondsToTime(seconds);
    }

    public static String formatMoney(int amount) {
        return TimeCore.formatMoney(amount);
    }

    public static String SCTToTime(double sct) {
        int seconds = (int) Math.ceil(sct / STCConfigServer.CONFIG.TIME_TO_MONEY.getAsInt());
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
        if (seconds < STCConfigServer.CONFIG.DANGER_TIME.getAsInt()) {
            hp = Math.max(-STCConfigServer.CONFIG.MIN_HP.getAsInt(), (seconds-STCConfigServer.CONFIG.DANGER_TIME.getAsInt())/STCConfigServer.CONFIG.DT_FOR_LOSE_1HP.getAsInt() - 1);
        } else if (seconds > STCConfigServer.CONFIG.SAFE_TIME.getAsInt()) {
            hp = Math.min(STCConfigServer.CONFIG.MAX_HP.getAsInt(), (seconds-STCConfigServer.CONFIG.SAFE_TIME.getAsInt())/STCConfigServer.CONFIG.DT_FOR_GAIN_1HP.getAsInt() + 1);
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

        return coins;
    }

    public static List<ItemStack> givenAmountOfCoins(int amount, int n) {
        LinkedHashMap<Coin,Integer> coins = new LinkedHashMap<>();

        // Build a map coin - amount
        int m = 0;
        for (Coin coin : Arrays.stream(Coin.values()).toList().reversed()) {
            // 1st value is amount of this coin, 2nd value is remainder
            Couple<Integer> coinAmount = coin.convert(amount);
            amount = coinAmount.getSecond();
            coins.put(coin, coinAmount.getFirst());
            m += coinAmount.getFirst();
        }

        while (m < n) {
            boolean changed = false;

            // Remove 1 of the largest available coin -> Convert to the equivalent amount of the coin below
            for (Map.Entry<Coin, Integer> entry : coins.entrySet()) {
                Coin coin = entry.getKey();
                int count = entry.getValue();

                if (count == 0 || coin == Coin.SPUR) continue;

                Coin smaller = Coin.values()[coin.ordinal() - 1];
                int ratio = coin.value / smaller.value;

                coins.put(coin, count - 1);
                coins.put(smaller, coins.getOrDefault(smaller, 0) + ratio);

                m += ratio - 1;

                changed = true;
                break;
            }

            if (!changed) break;
        }

        // Convert map to itemstack list
        List<ItemStack> items = new ArrayList<>();
        for (Map.Entry<Coin, Integer> entry : coins.entrySet()) {
            for (int i = 0; i < entry.getValue(); i++) {
                items.add(entry.getKey().asStack());
            }
        }
        return items;
    }

    public static void loseAndExplodeOnDeath(BankAccount account, Player player, ItemStack killerWeapon) {
        // Lose x% of total amount
        int amount = (int) (account.getBalance()*STCConfigServer.CONFIG.DEATH_LOSS.getAsDouble());
        account.deduct(amount);

        player.sendSystemMessage(Component.translatable("info.stonkstimecore.money_lost",amount).withStyle(ChatFormatting.RED));

        // Only p% of the full amount will be dropped
        // p depends on the looting level, 25% for level 0 up to 100% for level 3
        int lootingLevel = (killerWeapon != null) ? killerWeapon.getEnchantmentLevel(player.level().holderOrThrow(Enchantments.LOOTING)) : 0;
        int n = (int) (amount * Math.clamp((lootingLevel+1)/4f, 0, 1));

        // Drop items
        List<ItemStack> items = givenAmountOfCoins(n,30);
        for (ItemStack item : items) {
            player.drop(item, true, false);
        }
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

    public static void stackEffect(Player player, MobEffectInstance instance) {stackEffect(player,instance,false);}

    public static void stackEffect(Player player, MobEffectInstance effect, boolean shouldStackAmplifier) {
        int duration = effect.getDuration();
        int amplifier = effect.getAmplifier();
        if (player.hasEffect(effect.getEffect())) {
            duration += player.getEffect(effect.getEffect()).getDuration();
            amplifier += player.getEffect(effect.getEffect()).getAmplifier() + 1;
        }
        player.addEffect(new MobEffectInstance(
                effect.getEffect(),
                duration,
                shouldStackAmplifier ? amplifier : effect.getAmplifier(),
                effect.isAmbient(),
                effect.isVisible(),
                effect.showIcon()
        ));
    }

    public static byte[] UUIDToBytes(UUID uuid) {
        ByteBuffer buffer = ByteBuffer.allocate(16);

        buffer.putLong(uuid.getMostSignificantBits());
        buffer.putLong(uuid.getLeastSignificantBits());

        return buffer.array();
    }

    public static long getCurrentMinute() {
        return (new Date()).getTime() / 60000;
    }

    public static Instant getCurrentInstant() {
        return TimeCore.getCurrentInstant();
    }

    public static Instant truncateToHour(Instant instant) {
        return TimeCore.truncateToHour(instant);
    }

    public static Instant getCurrentHourStart() {
        return TimeCore.getCurrentHourStart();
    }

}

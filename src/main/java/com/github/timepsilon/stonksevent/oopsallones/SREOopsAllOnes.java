package com.github.timepsilon.stonksevent.oopsallones;

import com.github.timepsilon.stonksevent.AbstractRandomStonksEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class SREOopsAllOnes extends AbstractRandomStonksEvent {

    public SREOopsAllOnes(float weight, boolean isPositive, String combination, String name) {
        super(weight, isPositive, combination, name);
    }

    @Override
    public void onStart(Player player) {
        player.setHealth(1);
        player.getFoodData().setFoodLevel(1);

        for (ItemStack armor : player.getInventory().armor) {
            if (!armor.isEmpty() && armor.isDamageableItem()) {
                armor.setDamageValue(armor.getMaxDamage() - 1);
            }
        }

        for (ItemStack item : player.getInventory().items) {
            if (!item.isEmpty() && item.isDamageableItem()) {
                item.setDamageValue(item.getMaxDamage() - 1);
            }
        }

        for (ItemStack item : player.getInventory().offhand) {
            if (!item.isEmpty() && item.isDamageableItem()) {
                item.setDamageValue(item.getMaxDamage() - 1);
            }
        }
    }

    @Override
    public void onStop(@Nullable Player player) {

    }
}

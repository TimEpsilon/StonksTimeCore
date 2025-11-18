package com.github.timepsilon.items.custom;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import org.jetbrains.annotations.NotNull;

public class TimeGearItem extends Item {

    public TimeGearItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean hasCustomEntity(@NotNull ItemStack stack) {
        return true;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        return super.useOn(context);
    }
}

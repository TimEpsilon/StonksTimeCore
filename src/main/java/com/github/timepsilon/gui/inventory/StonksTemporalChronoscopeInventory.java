package com.github.timepsilon.gui.inventory;

import com.github.timepsilon.block.entity.server.StonksTemporalChronoscopeEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;

import java.util.ArrayList;
import java.util.List;

public class StonksTemporalChronoscopeInventory extends ItemStackHandler {

    private StonksTemporalChronoscopeEntity entity;

    public StonksTemporalChronoscopeInventory(StonksTemporalChronoscopeEntity be) {
        super(27+6);
        this.entity = be;
    }

    public List<ItemStack> getItemStacks() {
        List<ItemStack> itemStacks = new ArrayList<>();
        for (ItemStack itemStack : this.stacks) {
            if (!itemStack.equals(ItemStack.EMPTY)) {
                itemStacks.add(itemStack);
            }
        }
        return itemStacks;
    }

}

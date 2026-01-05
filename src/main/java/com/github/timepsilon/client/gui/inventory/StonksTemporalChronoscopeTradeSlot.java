package com.github.timepsilon.client.gui.inventory;

import com.github.timepsilon.client.gui.StonksTemporalChronoscopeMenu;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

public class StonksTemporalChronoscopeTradeSlot extends SlotItemHandler {

    private StonksTemporalChronoscopeMenu menu;

    public StonksTemporalChronoscopeTradeSlot(StonksTemporalChronoscopeMenu menu, IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
        this.menu = menu;
    }
}

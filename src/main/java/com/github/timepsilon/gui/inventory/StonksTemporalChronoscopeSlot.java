package com.github.timepsilon.gui.inventory;

import com.github.timepsilon.gui.StonksTemporalChronoscopeMenu;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

public class StonksTemporalChronoscopeSlot extends SlotItemHandler {

    private StonksTemporalChronoscopeMenu menu;

    public StonksTemporalChronoscopeSlot(StonksTemporalChronoscopeMenu menu, IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
        this.menu = menu;
    }
}

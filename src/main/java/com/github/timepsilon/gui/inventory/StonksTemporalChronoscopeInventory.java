package com.github.timepsilon.gui.inventory;

import com.github.timepsilon.block.entity.server.StonksTemporalChronoscopeEntity;
import net.neoforged.neoforge.items.ItemStackHandler;

public class StonksTemporalChronoscopeInventory extends ItemStackHandler {

    private StonksTemporalChronoscopeEntity entity;

    public StonksTemporalChronoscopeInventory(StonksTemporalChronoscopeEntity be) {
        super(27+6);
        this.entity = be;
    }

}

package com.github.timepsilon.gui;

import com.github.timepsilon.block.entity.server.StonksTemporalChronoscopeEntity;
import com.simibubi.create.foundation.gui.menu.MenuBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;

public class StonksTemporalChronoscopeMenu  extends MenuBase<StonksTemporalChronoscopeEntity> {

    public static final int BLOCK_INV_START_ID = 0;
    public static final int BLOCK_INV_END_ID = BLOCK_INV_START_ID + 27 - 1; // included
    public static final int MONEY_START_ID = BLOCK_INV_END_ID + 1;
    public static final int MONEY_END_ID = MONEY_START_ID + 6 - 1;
    public static final int PLAYER_INV_START_ID = MONEY_END_ID + 1;
    public static final int PLAYER_HOTBAR_END_ID = PLAYER_INV_START_ID + 9 - 1;
    public static final int PLAYER_INV_END_ID = PLAYER_INV_START_ID + 36 - 1;

    public StonksTemporalChronoscopeMenu(MenuType<?> type, int id, Inventory inv, RegistryFriendlyByteBuf extraData) {
        super(type, id, inv, extraData);
    }

    public StonksTemporalChronoscopeMenu(MenuType<?> type, int id, Inventory inv, StonksTemporalChronoscopeEntity be) {
        super(type, id, inv, be);
    }

    public static StonksTemporalChronoscopeMenu create(int id, Inventory inv, StonksTemporalChronoscopeEntity be) {
        return new StonksTemporalChronoscopeMenu(ModMenu.STONKS_TEMPORAL_CHRONOSCOPE.get(), id, inv, be);
    }

    @Override
    protected StonksTemporalChronoscopeEntity createOnClient(RegistryFriendlyByteBuf extraData) {
        ClientLevel world = Minecraft.getInstance().level;
        BlockEntity be = world.getBlockEntity(extraData.readBlockPos());
        if (be instanceof StonksTemporalChronoscopeEntity stcBE) {
            return stcBE;
        }
        return null;
    }

    @Override
    protected void initAndReadInventory(StonksTemporalChronoscopeEntity contentHolder) {}

    @Override
    protected void addSlots() {

        addPlayerSlots(40, 130);
    }

    @Override
    protected void saveData(StonksTemporalChronoscopeEntity contentHolder) {}

    @Override
    public @NotNull ItemStack quickMoveStack(Player player, int i) {
        Slot slot = this.slots.get(i);

        if (!slot.hasItem()) {
            return ItemStack.EMPTY;
        }

        ItemStack slotStack = slot.getItem();
        return slotStack;
    }
}

package com.github.timepsilon.client.gui;

import com.github.timepsilon.block.entity.server.StonksTemporalChronoscopeEntity;
import com.github.timepsilon.client.gui.inventory.StonksTemporalChronoscopeTradeSlot;
import com.simibubi.create.foundation.gui.menu.MenuBase;
import dev.ithundxr.createnumismatics.content.backend.Coin;
import dev.ithundxr.createnumismatics.content.coins.CoinItem;
import dev.ithundxr.createnumismatics.content.coins.SlotOutputMergingCoinBag;
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

public class StonksTemporalChronoscopeMenu extends MenuBase<StonksTemporalChronoscopeEntity> {

    public static final int BLOCK_INV_START_ID = 0; //0
    public static final int BLOCK_INV_END_ID = BLOCK_INV_START_ID + 27 - 1; // included, 26
    public static final int MONEY_START_ID = BLOCK_INV_END_ID + 1; // 27
    public static final int MONEY_END_ID = MONEY_START_ID + 6 - 1; // 32
    public static final int PLAYER_INV_START_ID = MONEY_END_ID + 1; // 33
    public static final int PLAYER_INV_END_ID = PLAYER_INV_START_ID + 36 - 1; // 68


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

        int x = 16;
        int y = 21;
        for (int i = BLOCK_INV_START_ID;  i <= BLOCK_INV_END_ID; i++) {
            if (i % 9 == 0 && i > 0) {
                x = 16;
                y += 18;
            }
            addSlot(new StonksTemporalChronoscopeTradeSlot(this, contentHolder.inventory, i, x, y));
            x += 18;
        }

        x = 34;
        y = 87;
        for(Coin coin : Coin.values()) {
            this.addSlot(new SlotOutputMergingCoinBag(contentHolder.coinBag, coin, x, y));
            x += 18;
        }

        addPlayerSlots(20, 130);
    }

    @Override
    protected void saveData(StonksTemporalChronoscopeEntity contentHolder) {}

    @Override
    public @NotNull ItemStack quickMoveStack(Player player, int i) {
        // Salut Zelytra c'est probablement ici que tu vas chercher à casser un truc
        Slot slot = this.slots.get(i);

        if (!slot.hasItem()) {
            return ItemStack.EMPTY;
        }

        ItemStack slotStack = slot.getItem();
        boolean success = false;

        if ((BLOCK_INV_START_ID <= i && i <= BLOCK_INV_END_ID)) {
            // In block inventory -> To player inventory
            success = moveItemStackTo(slotStack, PLAYER_INV_START_ID, PLAYER_INV_END_ID+1, false);

        } else if (PLAYER_INV_START_ID <= i && i <= PLAYER_INV_END_ID) {
            // In player inventory -> To block inventory
            success = moveItemStackTo(slotStack, BLOCK_INV_START_ID, BLOCK_INV_END_ID+1, false);

        } else if ((MONEY_START_ID <= i && i <= MONEY_END_ID)) {
            // In bank -> Player inventory
            ItemStack coinStack = CoinItem.clearDisplayedCount(slotStack); // Removes the tooltip displaying the full amount of money
            int count = coinStack.getCount();
            success = moveItemStackTo(coinStack, PLAYER_INV_START_ID, PLAYER_INV_END_ID+1, false);
            if (success) {slot.remove(count);}
        }

        if (slotStack.isEmpty()) {
            slot.set(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }

        return success ? slotStack : ItemStack.EMPTY;
    }




}

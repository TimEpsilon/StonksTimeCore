package com.github.timepsilon.block.entity.server;

import com.github.timepsilon.client.gui.StonksTemporalChronoscopeMenu;
import com.github.timepsilon.client.gui.inventory.StonksTemporalChronoscopeInventory;
import com.github.timepsilon.config.STCConfigServer;
import com.github.timepsilon.datamaps.DataMaps;
import com.github.timepsilon.datamaps.SCTMap;
import com.github.timepsilon.items.ModItems;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.item.ItemHelper;
import com.simibubi.create.foundation.sound.SoundScapes;
import dev.ithundxr.createnumismatics.content.backend.Coin;
import dev.ithundxr.createnumismatics.content.coins.MergingCoinBag;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.p3pp3rf1y.sophisticatedcore.init.ModCoreDataComponents;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

import static com.github.timepsilon.attributes.ModAttributes.SCT_FACTOR;

public class StonksTemporalChronoscopeEntity extends KineticBlockEntity implements MenuProvider {

    public StonksTemporalChronoscopeInventory inventory;
    public MergingCoinBag coinBag;

    public static final int MIN_SPEED = 30;
    private static final List<Item> COIN_LIST = Arrays.stream(Coin.values()).map(coin -> coin.asStack().getItem()).toList();

    public StonksTemporalChronoscopeEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
        inventory = new StonksTemporalChronoscopeInventory(this);
        coinBag = new MergingCoinBag();
    }

    @Override
    public void updateFromNetwork(float maxStress, float currentStress, int networkSize) {
        super.updateFromNetwork(maxStress, currentStress, networkSize);
    }

    public boolean isActive() {
        return Math.abs(this.getSpeed()) >= MIN_SPEED & !overStressed;
    }

    @Override
    public void tickAudio() {
        super.tickAudio();

        if (!isActive())
            return;
        float pitch = Mth.clamp((Math.abs(getSpeed()) / 256f) + .45f, .85f, 1f);
        SoundScapes.play(SoundScapes.AmbienceGroup.KINETIC, worldPosition, pitch);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.stonkstimecore.stonks_temporal_chronoscope");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        return StonksTemporalChronoscopeMenu.create(i, inventory, this);
    }

    @Override
    protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {

        if (!coinBag.isEmpty()) {
            tag.put("CoinBag", coinBag.save(new CompoundTag()));
        }

        if (!clientPacket) {
            tag.put("Inventory", inventory.serializeNBT(registries));
        }

        super.write(tag, registries, clientPacket);
    }

    @Override
    protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(tag, registries, clientPacket);

        coinBag.clear();
        if (tag.contains("CoinBag")) {
            coinBag.load(tag.getCompound("CoinBag"));
        }

        if (!clientPacket) {
            inventory.deserializeNBT(registries, tag.getCompound("Inventory"));
        }
    }

    public void dropContents(Level level, BlockPos pos) {
        ItemHelper.dropContents(level, pos, inventory);
        for (int i = Coin.values().length - 1; i >= 0; i--) {
            Coin coin = Coin.values()[i];
            ItemStack item = coinBag.asStack(coin);
            int amount = item.getCount();
            coinBag.subtract(coin, amount);
            if (amount > 0) {
                Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), item);
            }
        }
    }

    public void computeSCTAmount(Player player) {
        if (!isActive()) {
            return;
        }

        List<ItemStack> itemStacks = inventory.getItemStacks();

        AttributeInstance SCTAttribute = player.getAttribute(SCT_FACTOR);
        float factor = (SCTAttribute == null) ? 1 : (float) SCTAttribute.getValue();
        float sctAmount = 0.0F;
        int totalQuantity = 0;

        for (ItemStack itemStack : itemStacks) {

            // Handles vanilla containers
            if (itemStack.getComponents().has(DataComponents.CONTAINER)) {
                boolean isEmpty = true;
                for (ItemStack subItem : itemStack.getComponents().get(DataComponents.CONTAINER).nonEmptyItems()) {
                    sctAmount += destroyAndConvert(subItem);
                    totalQuantity += subItem.getCount();
                    isEmpty = false;
                }
                if (!isEmpty) continue;
            }

            // Backpacks and barrels for now are too much of a headache to handle, so we skip them
            if (itemStack.getComponents().has(ModCoreDataComponents.STORAGE_UUID.get())) {
                continue;
            }

            // Prevent named items of being converted
            if (!itemStack.getItem().getName(itemStack).equals(itemStack.getHoverName())) continue;

            // Prevent money itself from being converted
            if (COIN_LIST.contains(itemStack.getItem())) continue;

            // Prevent golden ticket conversion
            if (itemStack.getItem() == ModItems.GOLDEN_TICKET.get()) continue;

            // Convert to money
            totalQuantity += itemStack.getCount();
            sctAmount += destroyAndConvert(itemStack);
        }
        coinBag.add(Coin.SPUR, (int) (sctAmount *  factor));

        // Generate golden tickets
        while (totalQuantity > 0) {
            if (player.getRandom().nextFloat() < STCConfigServer.CONFIG.SCT_GOLDEN_TICKET_PROBABILITY.get()) {
                inventory.insertItem((int)(Math.random() * inventory.getSlots()), new ItemStack(ModItems.GOLDEN_TICKET.get(), 1), false);
            }
            totalQuantity -= 1;
        }


        notifyUpdate();
    }

    private float destroyAndConvert(ItemStack itemStack) {
        SCTMap sct = itemStack.getItemHolder().getData(DataMaps.SCT_MAP);
        if (sct != null) { // Removes the item if it has a SCT value and add it to total
            float amount = sct.SCT() * itemStack.getCount();
            itemStack.setCount(0);
            return amount;
        }
        return 0;
    }
}

package com.github.timepsilon.block.entity.server;

import com.github.timepsilon.Core;
import com.github.timepsilon.datamaps.DataMaps;
import com.github.timepsilon.datamaps.SCTMap;
import com.github.timepsilon.gui.ModMenu;
import com.github.timepsilon.gui.StonksTemporalChronoscopeMenu;
import com.github.timepsilon.gui.inventory.StonksTemporalChronoscopeInventory;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.sound.SoundScapes;
import dev.ithundxr.createnumismatics.content.backend.Coin;
import dev.ithundxr.createnumismatics.content.coins.MergingCoinBag;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class StonksTemporalChronoscopeEntity extends KineticBlockEntity implements MenuProvider {

    public StonksTemporalChronoscopeInventory inventory;
    public MergingCoinBag coinBag;

    public StonksTemporalChronoscopeEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
        inventory = new StonksTemporalChronoscopeInventory(this);
        coinBag = new MergingCoinBag();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void tickAudio() {
        super.tickAudio();

        if (getSpeed() < IRotate.SpeedLevel.MEDIUM.getSpeedValue())
            return;
        float pitch = Mth.clamp((Math.abs(getSpeed()) / 256f) + .45f, .85f, 1f);
        SoundScapes.play(SoundScapes.AmbienceGroup.KINETIC, worldPosition, pitch);
    }

    @Override
    public Component getDisplayName() {
        return Component.literal("Stonks Temporal Chronoscope");
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

    public float computeSCTAmount() {
        List<ItemStack> itemStacks = inventory.getItemStacks();
        float sctAmount = 0.0F;
        for (ItemStack itemStack : itemStacks) {
            SCTMap sct = itemStack.getItemHolder().getData(DataMaps.SCT_MAP);
            if (sct != null) { // Removes the item if it has a SCT value and add it to total
                sctAmount += sct.SCT() * itemStack.getCount();
                itemStack.setCount(0);
            }
        }
        coinBag.add(Coin.SPUR, (int) sctAmount);
        notifyUpdate();
        return sctAmount;
    }
}

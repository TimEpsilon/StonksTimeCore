package com.github.timepsilon.block.entity.server;

import com.github.timepsilon.client.gui.StonksTemporalChronoscopeMenu;
import com.github.timepsilon.client.gui.inventory.StonksTemporalChronoscopeInventory;
import com.github.timepsilon.config.STCConfigServer;
import com.github.timepsilon.database.SCTTransactionDatabase;
import com.github.timepsilon.datamaps.DataMaps;
import com.github.timepsilon.datamaps.SCTManager;
import com.github.timepsilon.datamaps.SCTMap;
import com.github.timepsilon.items.ModItems;
import com.github.timepsilon.utils.SCTMathUtils;
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
import net.minecraft.server.level.ServerPlayer;
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
import net.minecraft.world.level.storage.loot.providers.number.BinomialDistributionGenerator;
import net.p3pp3rf1y.sophisticatedcore.init.ModCoreDataComponents;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

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

    @Override
    public void lazyTick() {
        super.lazyTick();
        notifyUpdate(); // There probably is a better way to update the coin amount on every player's screen but I haven't found it
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
        if (!isActive()) return;

        // Init
        List<ItemStack> itemStacks = inventory.getItemStacks().stream().filter(itemStack -> !itemStack.isEmpty()).toList();
        HashMap<Item, Integer> amountMap = new HashMap<>();
        HashMap<Item, Float> moneyMap = new HashMap<>();

        // Factor for SCT Conversion
        AttributeInstance SCTAttribute = player.getAttribute(SCT_FACTOR);
        float factor = (SCTAttribute == null) ? 1 : (float) SCTAttribute.getValue();

        // Convert to SCT values
        handleItemsToMoney(itemStacks, factor, moneyMap, amountMap);

        // Generate golden tickets
        awardGoldenTicket(amountMap);

        // Log interaction
        SCTTransactionDatabase.getDatabase().sendTransactions((ServerPlayer) player, amountMap, moneyMap);

        notifyUpdate();
    }

    private void handleItemsToMoney(Collection<ItemStack> itemStacks, float factor, HashMap<Item, Float> moneyMap, HashMap<Item, Integer> amountMap) {
        // reloads only these items
        SCTManager.updateSCTMap(itemStacks.stream().map(ItemStack::getItem).collect(Collectors.toList()));

        for (ItemStack itemStack : itemStacks) {

            // Handles vanilla containers
            if (itemStack.getComponents().has(DataComponents.CONTAINER)) {
                boolean isEmpty = true;

                Collection<ItemStack> subItems = itemStack.getComponents().get(DataComponents.CONTAINER)
                        .stream().filter(istack -> !istack.isEmpty()).toList();

                // reloads subitems
                SCTManager.updateSCTMap(subItems.stream().map(ItemStack::getItem).collect(Collectors.toList()));

                for (ItemStack subItem : subItems) {
                    if (itemCheck(itemStack)) {
                        accumulateConversion(subItem, factor, moneyMap, amountMap);
                        isEmpty = false;
                    }
                }
                if (!isEmpty) continue;
            }

            if (itemCheck(itemStack)) accumulateConversion(itemStack, factor, moneyMap, amountMap);
        }

        float fullAmount = moneyMap.values().stream().reduce(0.0f, Float::sum);
        coinBag.add(Coin.SPUR, (int) fullAmount);
    }

    private void accumulateConversion(ItemStack itemStack, float factor, Map<Item, Float> moneyMap, Map<Item, Integer> amountMap) {
        // Convert to money
        Item item = itemStack.getItem();
        int tmpAmount = itemStack.getCount();
        float tmpMoney = destroyAndConvert(itemStack) * factor;

        if (tmpMoney == 0) return;

        amountMap.merge(item, tmpAmount, Integer::sum);
        moneyMap.merge(item, tmpMoney, Float::sum);
    }

    private boolean itemCheck(ItemStack itemStack) {
        // Backpacks and barrels for now are too much of a headache to handle, so we skip them
        if (itemStack.getComponents().has(ModCoreDataComponents.STORAGE_UUID.get())) return false;

        // Prevent named items of being converted
        if (!itemStack.getItem().getName(itemStack).equals(itemStack.getHoverName())) return false;

        // Prevent money itself from being converted
        if (COIN_LIST.contains(itemStack.getItem())) return false;

        // Prevent golden ticket conversion
        if (itemStack.getItem() == ModItems.GOLDEN_TICKET.get()) return false;

        return true;
    }

    private void awardGoldenTicket(HashMap<Item, Integer> amountMap) {
        int amountGoldenTicket = amountOfGoldenTickets(amountMap);
        while (amountGoldenTicket > 0) {
            inventory.insertItem((int)(level.getRandom().nextFloat() * inventory.getSlots()), new ItemStack(ModItems.GOLDEN_TICKET.get(), 1), false);
            amountGoldenTicket -= 1;
        }
    }

    private int amountOfGoldenTickets(HashMap<Item, Integer> amountMap) {
        int amount = 0;
        float SCTMax = 21600; // 6h, any amount above will cap the additional probability

        for (Map.Entry<Item, Integer> entry : amountMap.entrySet()) {
            float x = SCTManager.SCT_MAP.getOrDefault(entry.getKey(),0f) / SCTMax;
            double p = STCConfigServer.CONFIG.SCT_GOLDEN_TICKET_PROBABILITY.get()
                    + STCConfigServer.CONFIG.SCT_GOLDEN_TICKET_ADDITIONAL_PROBABILITY.get()
                    * Math.clamp(1-Math.pow(1-x,3), 0, 1); // Cubic ease out
            int n = entry.getValue();

            while (n > 0) {
                if (level.getRandom().nextFloat() < p) {
                    amount += 1;
                }
                n -= 1;
            }
        }
        return amount;
    }

    private float destroyAndConvert(ItemStack itemStack) {
        SCTMap sct = itemStack.getItemHolder().getData(DataMaps.SCT_MAP);
        int n = itemStack.getCount();
        if (sct != null) { // Removes the item if it has a SCT value and add it to total
            float amount;
            if (sct.SCT() > 0) {
                amount = computeReducingAmount(itemStack.getItem(), sct.SCT(), n);
                // Updating price
                SCTManager.SCT_MAP.put(itemStack.getItem(),
                        SCTMathUtils.currentPrice(
                                sct.SCT(),
                                SCTManager.AMOUNT_MAP.get(itemStack.getItem()) + n));
            } else {
                amount = computeRandomAmount(n);
            }
            itemStack.setCount(0);
            return amount;
        }
        return 0;
    }

    private float computeRandomAmount(int n) {
        float amount = 0;
        List<Float> sctValues = SCTManager.SCT_MAP.values().stream().toList();
        while (n > 0) {
            amount += Math.clamp(
                    sctValues.get(level.getRandom().nextInt(sctValues.size())),
                    0 , 21600);
            n -= 1;
        }
        return amount;
    }

    private float computeReducingAmount(Item item, float realPrice, int n) {
        int currentN = SCTManager.AMOUNT_MAP.getOrDefault(item,0);
        return SCTMathUtils.effectivePrice(realPrice, currentN, currentN+n);
    }


}

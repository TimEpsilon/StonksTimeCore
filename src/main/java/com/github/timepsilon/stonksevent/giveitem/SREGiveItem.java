package com.github.timepsilon.stonksevent.giveitem;

import com.github.timepsilon.Core;
import com.github.timepsilon.stonksevent.AbstractRandomStonksEvent;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static com.github.timepsilon.utils.TimeUtils.givePlayer;

public class SREGiveItem extends AbstractRandomStonksEvent {

    public SREGiveItem(float weight, boolean isPositive, String combination, String name) {
        super(weight, isPositive, combination, name);
    }

    @Override
    public void onStart(Player player) {
        ResourceKey<LootTable> lootKey = ResourceKey.create(
                Registries.LOOT_TABLE,
                ResourceLocation.fromNamespaceAndPath(Core.MODID, "sre/give_item/give_item"));
        LootTable lootTable = player.getServer().reloadableRegistries().getLootTable(lootKey);

        LootParams params = new LootParams.Builder((ServerLevel) player.level())
                .withParameter(LootContextParams.THIS_ENTITY, player)
                .withParameter(LootContextParams.ORIGIN, player.position())
                .withLuck(player.getLuck())
                .create(LootContextParamSets.GIFT);

        List<ItemStack> items = lootTable.getRandomItems(params);

        for (ItemStack item : items) {
            givePlayer(player, item);
        }

    }

    @Override
    public void onStop(@Nullable Player player) {

    }
}

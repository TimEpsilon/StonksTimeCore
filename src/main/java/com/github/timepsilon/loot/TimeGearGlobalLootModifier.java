package com.github.timepsilon.loot;

import com.github.timepsilon.Core;
import com.github.timepsilon.items.ModItems;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.architectury.event.events.common.LootEvent;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;
import net.neoforged.neoforge.event.LootTableLoadEvent;

import java.awt.*;
import java.util.List;
import java.util.regex.Pattern;

public class TimeGearGlobalLootModifier extends LootModifier {

    public static TimeGearGlobalLootModifier INSTANCE;

    public static final MapCodec<TimeGearGlobalLootModifier> CODEC = RecordCodecBuilder.mapCodec(instance ->
            LootModifier.codecStart(instance)
                    .and(Codec.FLOAT.fieldOf("probability").forGetter(TimeGearGlobalLootModifier::getProbability))
                    .and(Codec.list(Codec.STRING).fieldOf("whitelist").forGetter(TimeGearGlobalLootModifier::getWhitelist))
                    .and(Codec.list(Codec.STRING).fieldOf("blacklist").forGetter(TimeGearGlobalLootModifier::getBlacklist))
                    .apply(instance, TimeGearGlobalLootModifier::new)
    );

    private final float probability;
    private final List<String> whitelist;
    private final List<String> blacklist;
    private final List<Pattern> whitelistPatterns;
    private final List<Pattern> blacklistPatterns;

    protected TimeGearGlobalLootModifier(LootItemCondition[] conditionsIn, float probability, List<String> whitelist, List<String> blacklist) {
        super(conditionsIn);
        this.probability = probability;
        this.whitelist = whitelist;
        this.blacklist = blacklist;
        this.whitelistPatterns = whitelist.stream().map(Pattern::compile).toList();
        this.blacklistPatterns = blacklist.stream().map(Pattern::compile).toList();

        INSTANCE = this;
    }

    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext lootContext) {
        String id = lootContext.getQueriedLootTableId().toString();

        // Only if loot not on blacklist and in whitelist
        if (!canGenerate(id)) return generatedLoot;

        // Luck influences the result
        if (lootContext.getRandom().nextFloat() < probability * (1+lootContext.getLuck())) {
            generatedLoot.add(new ItemStack(ModItems.TIME_GEAR.get()));

            Vec3 vec = lootContext.getParamOrNull(LootContextParams.ORIGIN);
            Entity entity = lootContext.getParamOrNull(LootContextParams.THIS_ENTITY);
            Core.LOGGER.info("{} Has discovered a Time Gear at {} ({})", entity, vec, id);

            for (ServerPlayer p : lootContext.getLevel().getServer().getPlayerList().getPlayers()) {
                // Global chat message
                p.sendSystemMessage(
                        Component.translatable("info.stonkstimecore.time_gear_found")
                                .withStyle(ChatFormatting.AQUA));
            }
        }
        return generatedLoot;
    }

    @Override
    public MapCodec<? extends IGlobalLootModifier> codec() {
        return CODEC;
    }

    public float getProbability() { return this.probability; }

    public List<String> getWhitelist() { return this.whitelist; }

    public List<String> getBlacklist() { return this.blacklist; }

    public boolean isBlacklisted(String id) {
        return blacklistPatterns.stream().anyMatch(p -> p.matcher(id).matches());
    }

    public boolean isWhitelisted(String id) {
        return whitelistPatterns.stream().anyMatch(p -> p.matcher(id).matches());
    }

    public boolean canGenerate(String id) {
        boolean blacklisted = isBlacklisted(id);
        boolean whitelisted = isWhitelisted(id);
        return (!blacklisted) & (whitelisted);
    }
}

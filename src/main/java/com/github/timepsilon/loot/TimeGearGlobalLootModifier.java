package com.github.timepsilon.loot;

import com.github.timepsilon.items.ModItems;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;

public class TimeGearGlobalLootModifier extends LootModifier {

    public static final MapCodec<TimeGearGlobalLootModifier> CODEC = RecordCodecBuilder.mapCodec(instance ->
            LootModifier.codecStart(instance)
                    .and(Codec.FLOAT.fieldOf("probability").forGetter(TimeGearGlobalLootModifier::getProbability))
                    .apply(instance, TimeGearGlobalLootModifier::new)
    );

    private final float probability;

    protected TimeGearGlobalLootModifier(LootItemCondition[] conditionsIn, float probability) {
        super(conditionsIn);
        this.probability = probability;
    }

    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext lootContext) {
        if (lootContext.getRandom().nextFloat() < probability) {
            generatedLoot.add(new ItemStack(ModItems.TIME_GEAR.get()));
        }
        return generatedLoot;
    }

    @Override
    public MapCodec<? extends IGlobalLootModifier> codec() {
        return CODEC;
    }

    public float getProbability() {
        return this.probability;
    }
}

package com.github.timepsilon.stonksevent.spawnmob;

import com.github.timepsilon.Core;
import com.github.timepsilon.stonksevent.AbstractRandomStonksEvent;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.List;

public class SRESpawnMob extends AbstractRandomStonksEvent {

    private static final TagKey<EntityType<?>> MOB_POOL_TAG = TagKey.create(
            Registries.ENTITY_TYPE,
            ResourceLocation.fromNamespaceAndPath(Core.MODID, "random_mob")
    );

    public SRESpawnMob(float weight, boolean isPositive, String combination, String name) {
        super(weight, isPositive, combination, name);
    }

    @Override
    public void onStart(Player player) {
        List<EntityType<?>> pool = getEntityTypes();
        if (pool.isEmpty()) return;

        Level level = player.level();

        EntityType<?> sample = pool.get(level.random.nextInt(pool.size()));

        Entity entity = sample.create(level);
        if (entity != null) {
            entity.moveTo(player.getX(), player.getY(), player.getZ());
            level.addFreshEntity(entity);
        }
    }

    @Override
    public void onStop(Player player) {

    }

    private static List<EntityType<?>> getEntityTypes() {
        Registry<EntityType<?>> registry = BuiltInRegistries.ENTITY_TYPE;
        return registry.stream().filter(type -> type.is(MOB_POOL_TAG)).toList();
    }
}

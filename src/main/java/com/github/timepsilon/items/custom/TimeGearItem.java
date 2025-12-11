package com.github.timepsilon.items.custom;

import com.github.timepsilon.entity.ModEntities;
import com.github.timepsilon.entity.custom.TimeGearEntity;
import com.github.timepsilon.items.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.server.players.OldUsersConverter;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class TimeGearItem extends Item {

    public TimeGearItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean hasCustomEntity(@NotNull ItemStack stack) {
        return true;
    }

    // Taken from the armor stand item class / Package entity
    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level world = context.getLevel();
        if (world.isClientSide) return InteractionResult.SUCCESS;

        BlockPos pos = context.getClickedPos();
        Direction direction = context.getPlayer().getDirection();
        Vec3 spawn = Vec3.atCenterOf(pos).add(0,2,0);

        EntityType<TimeGearEntity> type = ModEntities.TIME_GEAR.get();
        TimeGearEntity timeGearEntity = type.create(world);
        timeGearEntity.moveTo(spawn.x, spawn.y, spawn.z);
        timeGearEntity.absRotateTo(direction.toYRot(), 0);

        ItemStack itemInHand = context.getItemInHand();

        // If the player is in creative, the item name becomes the name of the player to link the gear to
        if (context.getPlayer().isCreative()) {
            Component playerName = itemInHand.getHoverName();
            UUID uuid;

            if (!Component.translatable("item.stonkstimecore.time_gear").equals(playerName)) {
                uuid = UUIDUtil.createOfflinePlayerUUID(playerName.toString());
            } else {
                uuid = context.getPlayer().getUUID();
            }

            timeGearEntity.setPlayerUUID(uuid);

        } else {
            timeGearEntity.setPlayer(context.getPlayer());
        }

        world.addFreshEntity(timeGearEntity);


        itemInHand.shrink(1);
        return InteractionResult.SUCCESS;
    }


}

package com.github.timepsilon.commands.timegear;

import com.github.timepsilon.entity.custom.TimeGearEntity;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import dev.ithundxr.createnumismatics.Numismatics;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.UuidArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.*;
import net.neoforged.neoforge.common.UsernameCache;

import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class TimeGearLogic {

    private static final double RANGE = 5;

    public static CompletableFuture<Suggestions> suggestLookedAtGear(CommandContext<CommandSourceStack> ctx, SuggestionsBuilder builder) {
        Entity viewer = ctx.getSource().getEntity();
        if (viewer != null) {
            TimeGearEntity gear = getLookedAtGear(viewer);
            if (gear != null) {
                String uuid = gear.getUUID().toString();
                if (uuid.toLowerCase(Locale.ROOT).startsWith(builder.getRemainingLowerCase())) {
                    String owner = gear.getOwnerName();
                    builder.suggest(uuid, Component.literal(
                            "TimeGear" + (owner != null ? " (owner: " + owner + ")" : " (no owner)")));
                }
            }
        }
        return builder.buildFuture();
    }

    public static CompletableFuture<Suggestions> suggestBankPlayers(
            CommandContext<CommandSourceStack> ctx, SuggestionsBuilder builder) {

        for (UUID uuid : Numismatics.BANK.accounts.keySet()) {
            String str = uuid.toString();
            if (!str.toLowerCase(Locale.ROOT).startsWith(builder.getRemainingLowerCase())) continue;

            String name = UsernameCache.getLastKnownUsername(uuid);
            if (name != null) builder.suggest(str, Component.literal(name));
            else builder.suggest(str);
        }
        return builder.buildFuture();
    }

    public static int setOwner(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        UUID gearUUID = UuidArgument.getUuid(ctx, "gear");
        UUID playerUUID = UuidArgument.getUuid(ctx, "player");

        TimeGearEntity gear = null;
        for (ServerLevel level : ctx.getSource().getServer().getAllLevels()) {
            Entity e = level.getEntity(gearUUID);
            if (e instanceof TimeGearEntity tg) {
                gear = tg;
                break;
            }
        }

        if (gear == null) {
            ctx.getSource().sendFailure(Component.literal("No loaded TimeGear found with UUID " + gearUUID));
            return 0;
        }

        gear.setPlayerUUID(playerUUID);

        String name = UsernameCache.getLastKnownUsername(playerUUID);
        String display = name != null ? name + " (" + playerUUID + ")" : playerUUID.toString();
        ctx.getSource().sendSuccess(() -> Component.literal("TimeGear owner set to " + display), true);
        return 1;
    }

    private static TimeGearEntity getLookedAtGear(Entity viewer) {
        Vec3 eye = viewer.getEyePosition();
        Vec3 look = viewer.getViewVector(1.0F);
        Vec3 end = eye.add(look.scale(RANGE));

        BlockHitResult blockHit = viewer.level().clip(
                new ClipContext(eye, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, viewer));
        if (blockHit.getType() != HitResult.Type.MISS) {
            end = blockHit.getLocation();
        }

        AABB box = viewer.getBoundingBox().expandTowards(look.scale(RANGE)).inflate(1.0D);
        EntityHitResult hit = ProjectileUtil.getEntityHitResult(
                viewer.level(), viewer, eye, end, box, e -> e instanceof TimeGearEntity);

        return hit != null ? (TimeGearEntity) hit.getEntity() : null;
    }
}

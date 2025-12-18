package com.github.timepsilon.commands.isout;

import com.github.timepsilon.time.PlayerOutData;
import com.github.timepsilon.time.TimerHandler;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public class OutLogic {

    public static int getOut(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = EntityArgument.getPlayer(ctx, "player");
        PlayerOutData timer = PlayerOutData.getPlayerTimer(ctx.getSource().getServer());

        boolean isOut = timer.isOut(player.getUUID());
        ctx.getSource().sendSuccess(() -> Component.literal(String.valueOf(isOut)), false);
        return isOut ? 1 : 0;
    }

    public static int setOut(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = EntityArgument.getPlayer(ctx, "player");
        PlayerOutData timer = PlayerOutData.getPlayerTimer(ctx.getSource().getServer());
        boolean out = BoolArgumentType.getBool(ctx, "boolean");

        timer.setOut(player.getUUID(), out);
        ctx.getSource().sendSuccess(() -> Component.translatable("commands.stonkstimecore.update_out", player.getName(), String.valueOf(out)), false);
        return out ? 1 : 0;
    }
}

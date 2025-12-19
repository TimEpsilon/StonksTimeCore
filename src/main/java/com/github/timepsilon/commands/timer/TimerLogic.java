package com.github.timepsilon.commands.timer;

import com.github.timepsilon.time.PlayerOutData;
import com.github.timepsilon.time.TimeManager;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.ithundxr.createnumismatics.Numismatics;
import dev.ithundxr.createnumismatics.content.backend.BankAccount;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class TimerLogic {

    public static int getTime(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = EntityArgument.getPlayer(ctx, "player");
        BankAccount account = Numismatics.BANK.getAccount(player.getUUID());

        int seconds = account.getBalance() / TimeManager.TIME_TO_MONEY;
        ctx.getSource().sendSuccess(() -> Component.literal(String.valueOf(TimeManager.secondsToTime(seconds))), false);
        return seconds;
    }

    public static int setTime(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = EntityArgument.getPlayer(ctx, "player");
        BankAccount account = Numismatics.BANK.getAccount(player.getUUID());
        int seconds = IntegerArgumentType.getInteger(ctx, "value");

        account.setBalance(seconds*TimeManager.TIME_TO_MONEY);
        ctx.getSource().sendSuccess(() -> Component.translatable("commands.stonkstimecore.set_time",player.getName(),TimeManager.secondsToTime(seconds)), false);
        return seconds;
    }

    public static int addTime(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = EntityArgument.getPlayer(ctx, "player");
        BankAccount account = Numismatics.BANK.getAccount(player.getUUID());
        int seconds = IntegerArgumentType.getInteger(ctx, "value");

        if (seconds > 0) {
            account.deposit(seconds*TimeManager.TIME_TO_MONEY);
        } else {
            account.deduct(-seconds*TimeManager.TIME_TO_MONEY);
        }
        ctx.getSource().sendSuccess(() -> Component.translatable("commands.stonkstimecore.add_time",seconds,player.getName()), false);
        return seconds;
    }
}

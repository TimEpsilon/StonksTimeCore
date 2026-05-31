package com.github.timepsilon.commands.timer;

import com.github.timepsilon.time.TimerHandler;
import com.github.timepsilon.utils.TimeUtils;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.ithundxr.createnumismatics.Numismatics;
import dev.ithundxr.createnumismatics.content.backend.BankAccount;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.GameProfileArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collection;

import static com.github.timepsilon.commands.isout.OutLogic.tryGettingPlayer;

public class TimerLogic {

    public static int getTime(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Collection<GameProfile> players = GameProfileArgument.getGameProfiles(ctx, "player");
        int seconds = players.stream()
                .map(player -> Numismatics.BANK.getOrCreateAccount(player.getId(), BankAccount.Type.PLAYER))
                .mapToInt(account -> account.getBalance() / TimeUtils.TIME_TO_MONEY)
                .sum();

        ctx.getSource().sendSuccess(() -> Component.literal(TimeUtils.secondsToTime(seconds)), false);
        return seconds;
    }

    public static int setTime(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Collection<GameProfile> players = GameProfileArgument.getGameProfiles(ctx, "player");
        int seconds = IntegerArgumentType.getInteger(ctx, "value");

        for (GameProfile player : players) {
            BankAccount account = Numismatics.BANK.getOrCreateAccount(player.getId(), BankAccount.Type.PLAYER);
            account.setBalance(seconds* TimeUtils.TIME_TO_MONEY);
            ctx.getSource().sendSuccess(() -> Component.translatable("commands.stonkstimecore.set_time",player.getName(), TimeUtils.secondsToTime(seconds)), false);
        }
        return seconds;
    }

    public static int addTime(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Collection<GameProfile> players = GameProfileArgument.getGameProfiles(ctx, "player");
        int seconds = IntegerArgumentType.getInteger(ctx, "value");

        for (GameProfile player : players) {
            BankAccount account = Numismatics.BANK.getOrCreateAccount(player.getId(), BankAccount.Type.PLAYER);
            ServerPlayer sPlayer = tryGettingPlayer(ctx.getSource().getServer(), player.getId());
            if (seconds > 0) {
                account.deposit(seconds* TimeUtils.TIME_TO_MONEY);
                TimerHandler.sendInfoPacket(sPlayer, "+"+(seconds*TimeUtils.TIME_TO_MONEY)+"\u9000");
            } else {
                account.deduct(-seconds* TimeUtils.TIME_TO_MONEY);
                TimerHandler.sendInfoPacket(sPlayer, (seconds*TimeUtils.TIME_TO_MONEY)+"\u9000");
            }
            ctx.getSource().sendSuccess(() -> Component.translatable("commands.stonkstimecore.add_time",seconds,player.getName()), false);
        }
        return seconds;
    }
}

package com.github.timepsilon.commands.isout;

import com.github.timepsilon.time.PlayerOutData;
import com.github.timepsilon.time.PlayerOutHandler;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.GameProfileArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collection;
import java.util.UUID;

public class OutLogic {

    public static int getOut(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Collection<GameProfile> players = GameProfileArgument.getGameProfiles(ctx, "player");
        PlayerOutData timer = PlayerOutData.getPlayerOutData(ctx.getSource().getServer());

        boolean isOut = players.stream()
                .map(player -> timer.isOut(player.getId()))
                .reduce(true, (a, b) -> a && b);

        ctx.getSource().sendSuccess(() -> Component.literal(String.valueOf(isOut)), false);
        return isOut ? 1 : 0;
    }

    public static int setOut(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Collection<GameProfile> players = GameProfileArgument.getGameProfiles(ctx, "player");
        boolean out = BoolArgumentType.getBool(ctx, "boolean");

        for (GameProfile player : players) {
            ServerPlayer sPlayer = tryGettingPlayer(ctx.getSource().getServer(), player.getId());
            if (sPlayer != null) {
                // player is online -> add client effects
                PlayerOutHandler.setOut(sPlayer, out);
            } else {
                // player is offline -> purely server side
                PlayerOutHandler.setOut(player.getId(), out);
            }


            ctx.getSource().sendSuccess(() -> Component.translatable("commands.stonkstimecore.update_out", player.getName(), String.valueOf(out)), false);
        }

        return out ? 1 : 0;
    }

    public static ServerPlayer tryGettingPlayer(MinecraftServer server, UUID uuid) {
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            if (player.getUUID().equals(uuid)) {
                return player;
            }
        }
        return null;
    }
}

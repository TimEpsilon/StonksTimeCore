package com.github.timepsilon.commands.events;

import com.github.timepsilon.stonksevent.AbstractRandomStonksEvent;
import com.github.timepsilon.stonksevent.StonksEventManager;
import com.github.timepsilon.stonksevent.StonksEventType;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.GameProfileArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class EventsLogic {

    public static int listEvents(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        HashMap<AbstractRandomStonksEvent,Float> currentEvents = StonksEventManager.getCurrentEventsTimer();

        int count = currentEvents.size();
        ctx.getSource().sendSuccess(() -> Component.translatable("commands.stonkstimecore.events_list", count), false);

        for (Map.Entry<AbstractRandomStonksEvent,Float> entry : currentEvents.entrySet()) {
                ctx.getSource().sendSuccess(() -> Component.literal(
                        String.format("%s : %ss",
                                entry.getKey().getName().toUpperCase(),
                                Math.round(entry.getValue()))),
                        false);
        }
        return count;
    }

    public static int addEvent(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        String name = StringArgumentType.getString(ctx, "event");
        StonksEventType type = StonksEventType.valueOf(name);

        Collection<GameProfile> players = GameProfileArgument.getGameProfiles(ctx, "player");

        for (GameProfile gProfile : players) {
            Player player = ctx.getSource().getServer().getPlayerList().getPlayers().stream().filter(
                    p -> p.getUUID().equals(gProfile.getId())).findFirst().orElse(null);

            if (player != null) {
                StonksEventType.startGivenEvent(player, type);
                ctx.getSource().sendSuccess(() -> Component.translatable("commands.stonkstimecore.add_event", type.name(), player.getName()), true);
            } else {
                ctx.getSource().sendFailure(Component.translatable("commands.stonkstimecore.add_event_fail"));
                return 0;
            }
        }
        return type.ordinal();
    }

    public static int removeEvent(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        String name = StringArgumentType.getString(ctx, "event");
        StonksEventType type = StonksEventType.valueOf(name.toUpperCase());

        Collection<GameProfile> players = GameProfileArgument.getGameProfiles(ctx, "player");

        for (GameProfile gProfile : players) {
            Player player = ctx.getSource().getServer().getPlayerList().getPlayers().stream().filter(
                    p -> p.getUUID().equals(gProfile.getId())).findFirst().orElse(null);

            if ((player != null) && (StonksEventManager.isEventRunning(type))) {
                StonksEventType.stopGivenEvent(player, type);
                ctx.getSource().sendSuccess(() -> Component.translatable("commands.stonkstimecore.remove_event", type.name(), player.getName()), true);
            } else {
                ctx.getSource().sendFailure(Component.translatable("commands.stonkstimecore.remove_event_fail"));
                return 0;
            }
        }
        return type.ordinal();
    }

}

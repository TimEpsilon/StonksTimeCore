package com.github.timepsilon.commands;


import com.github.timepsilon.commands.equivalency.GenerateEquivalency;
import com.github.timepsilon.commands.isout.OutLogic;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;

public class STCCommand {

    /**
     * The admin command for the mod
     * <p>
     * Syntax is as follows :
     * <p>
     * /stc (equiv|timer|out) ...
     * <ul>
     *     <li>... equiv generate</li>
     *     <li>... timer &lt;player&gt; (get|set|add)</li>
     *     <li>... out &lt;player&gt; (get|set)</li>
     * </ul>
     */
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {

        LiteralArgumentBuilder<CommandSourceStack> literalargumentbuilder =
                Commands.literal("stc").requires(p -> p.hasPermission(2))
                        .then(Commands.literal("equiv")
                                .then(Commands.literal("generate")
                                        .executes(GenerateEquivalency::generateFiles)))
                        .then(Commands.literal("timer")
                                .then(Commands.argument("player", EntityArgument.player())
                                        .then(Commands.literal("get")
                                                .executes((ctx) -> 1))
                                        .then(Commands.literal("set")
                                                .executes((ctx) -> 2))
                                        .then(Commands.literal("add")
                                                .executes((ctx) -> 3))))
                        .then(Commands.literal("out")
                                .then(Commands.argument("player", EntityArgument.player())
                                        .then(Commands.literal("get")
                                                .executes(OutLogic::getOut))
                                        .then(Commands.literal("set")
                                                .then(Commands.argument("boolean", BoolArgumentType.bool())
                                                        .executes(OutLogic::setOut)))));


        dispatcher.register(literalargumentbuilder);
    }


}

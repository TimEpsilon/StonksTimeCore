package com.github.timepsilon.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public class ItemGetter {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("itemgetter")
                .requires(p -> p.hasPermission(2))
                .executes(
                        p -> iterateItems(
                                p.getSource()
                        )
                )
        );
    }

    private static int iterateItems(CommandSourceStack source) throws CommandSyntaxException {
        for (ResourceLocation itemId : BuiltInRegistries.ITEM.keySet()) {
            Item item = BuiltInRegistries.ITEM.get(itemId);
            if (itemId.getNamespace().equals("stonkstimecore")) {
                // Log or handle the item
                System.out.println("Item ID: " + itemId + ", Item: " + item);
            }
        }
        return 0;
    }

}


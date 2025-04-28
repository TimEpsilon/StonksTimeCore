package com.github.timepsilon.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class GenerateEquivalency {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("equivalency")
                .requires(p -> p.hasPermission(2))
                .executes(
                        GenerateEquivalency::generateFiles
                )
        );
    }

    private static int generateFiles(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        CommandSourceStack source = ctx.getSource();
        try {
            iterateItems();
            iterateRecipes(source);
        } catch (IOException e) {e.printStackTrace();}
        return 0;
    }

    private static void iterateItems() throws IOException {
        ArrayList<String> items = new ArrayList<>();
        for (ResourceLocation itemId : BuiltInRegistries.ITEM.keySet()) {
            items.add(itemId.toString());
        }
        Files.write(Path.of("C:/Users/timde/Documents/items.txt"), items);
    }

    private static void iterateRecipes(CommandSourceStack source) {
        RecipeManager recipeManager = source.getServer().getRecipeManager();

        Collection<RecipeHolder<?>> recipes = recipeManager.getRecipes();

        for (RecipeHolder<?> holder : recipes) {
            Recipe<?> recipe = holder.value();
            ResourceLocation id = holder.id();
            RecipeType<?> type = recipe.getType();

            System.out.println("Recipe ID: " + id + " Type: " + type);

            // Try to extract inputs
            List<ItemStack> inputs = getInputs(recipe);
            System.out.println("  Inputs:");
            System.out.println(inputs);

            // Try to extract output
            ItemStack output = recipe.getResultItem(source.getServer().registryAccess());
            if (!output.isEmpty()) {
                System.out.println(output);
            } else {
                System.out.println("  Output: <none>");
            }
        }
    }

    private static List<ItemStack> getInputs(Recipe<?> recipe) {
        List<ItemStack> inputs = new ArrayList<>();
        List<Ingredient> ingredients = recipe.getIngredients();
        for (Ingredient ingredient : ingredients) {
            inputs.addAll(Arrays.asList(ingredient.getItems()));
        }
        return inputs;
    }

}


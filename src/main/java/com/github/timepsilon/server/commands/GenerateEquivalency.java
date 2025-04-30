package com.github.timepsilon.server.commands;

import com.google.gson.Gson;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
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

    private static void iterateRecipes(CommandSourceStack source) throws IOException {
        // Getting the recipes
        RecipeManager recipeManager = source.getServer().getRecipeManager();
        Collection<RecipeHolder<?>> recipes = recipeManager.getRecipes();

        // Soon to be JSON mapping the recipe ids to their input, output and type
        HashMap<String, Object> recipeMap = new HashMap<>();

        for (RecipeHolder<?> holder : recipes) {
            // Getting the real recipe object out of the holder
            Recipe<?> recipe = holder.value();

            HashMap<String,Object> singleRecipeDict = new HashMap<>();

            // Extract a map of items <-> amount
            HashMap<String, Integer> inputMap = getInputs(recipe);

            // Extract a map of items <-> amount
            HashMap<String, Integer> outputMap = new HashMap<>();
            ItemStack output = recipe.getResultItem(source.getServer().registryAccess());
            if (!output.isEmpty()) {
                outputMap.put(output.getItem().toString(), output.getCount());
            }

            // Constructing the dict for a single recipe
            singleRecipeDict.put("type", recipe.getType().toString());
            singleRecipeDict.put("input", inputMap);
            singleRecipeDict.put("output", outputMap);

            // Adding the dict to the main dict under the key id
            recipeMap.put(holder.id().toString(), singleRecipeDict);
        }

        // Serialization
        Gson gson = new Gson();
        String json = gson.toJson(recipeMap);

        // Saving
        BufferedWriter writer = new BufferedWriter(new FileWriter("C:/Users/timde/Documents/recipes.json"));
        writer.write(json);
        writer.close();

    }

    private static HashMap<String, Integer> getInputs(Recipe<?> recipe) {
        List<Ingredient> ingredients = recipe.getIngredients();
        HashMap<String, Integer> inputMap = new HashMap<>();
        for (Ingredient ingredient : ingredients) {
            System.out.println(ingredient.getItems());
            if (ingredient.getItems().length > 0) {
                inputMap.put(ingredient.getItems()[0].getItem().toString(), ingredient.getItems().length);
            }
        }
        return inputMap;
    }

}


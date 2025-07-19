package com.github.timepsilon.server.commands.equivalency;

import com.github.timepsilon.server.commands.equivalency.io.create.*;
import com.github.timepsilon.server.commands.equivalency.io.minecraft.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
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
        Files.write(Path.of("../items.txt"), items);
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
            String mod = holder.id().getNamespace();

            HashMap<String,Object> singleRecipeDict = new HashMap<>();

            // Extract a map of items <-> amount
            HashMap<String, HashMap<String, Integer>> inputMap;
            if (mod.equals("create")) {
                inputMap = RecipeInputCreate.getInputs(recipe);
            } else {
                inputMap = RecipeInputDefault.getInputs(recipe);
            }

            // Extract a map of items <-> amount
            HashMap<String, Integer> outputMap;
            if (mod.equals("create")) {
                outputMap = RecipeOutputCreate.getOutputs(recipe, source.getServer().registryAccess());
            } else {
                outputMap = RecipeOutputDefault.getOutputs(recipe, source.getServer().registryAccess());
            }

            // Constructing the dict for a single recipe
            singleRecipeDict.put("type", recipe.getType().toString());
            singleRecipeDict.put("input", inputMap);
            singleRecipeDict.put("output", outputMap);

            // Adding the dict to the main dict under the key id
            recipeMap.put(holder.id().toString(), singleRecipeDict);
        }

        // Serialization
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(recipeMap);

        // Saving
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("../recipes.json"))) {
            writer.write(json);
        }

    }


}


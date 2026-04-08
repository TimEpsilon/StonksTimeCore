package com.github.timepsilon.commands.equivalency;

import com.github.timepsilon.utils.FileManager;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class GenerateEquivalency {

    public static int generateFiles(CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack source = ctx.getSource();
        iterateItems(source);
        iterateRecipes(source);
        iterateTags(source);
        source.sendSuccess(() -> Component.translatable("commands.stonkstimecore.generate_equivalency").withStyle(ChatFormatting.DARK_GRAY), true);
        return Command.SINGLE_SUCCESS;
    }

    private static void iterateItems(CommandSourceStack source) {
        ArrayList<String> items = new ArrayList<>();
        for (ResourceLocation itemId : BuiltInRegistries.ITEM.keySet()) {
            items.add(itemId.toString());
        }
        FileManager.writeFileOnWorld("items.txt", items, source.getServer());
    }

    private static void iterateRecipes(CommandSourceStack source) {
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
            inputMap = RecipeInput.getInputs(recipe);

            // Extract a map of items <-> amount
            HashMap<String, Integer> outputMap;
            outputMap = RecipeOutput.getOutputs(recipe, source.getServer().registryAccess());

            // Constructing the dict for a single recipe
            singleRecipeDict.put("type", recipe.getType().toString());
            singleRecipeDict.put("input", inputMap);
            singleRecipeDict.put("output", outputMap);

            // Adding the dict to the main dict under the key id
            recipeMap.put(holder.id().toString(), singleRecipeDict);
        }

        FileManager.writeFileOnWorld("recipes.json", recipeMap, source.getServer());

    }

    private static void iterateTags(CommandSourceStack source) {
        Registry<Item> itemRegistry = source.getLevel().registryAccess().registry(Registries.ITEM).orElseThrow();
        HashMap<String, List<String>> tagMap = new HashMap<>();

        itemRegistry.getTags().forEach(pair -> {
            TagKey<Item> tag = pair.getFirst();
            HolderSet.Named<Item> content = pair.getSecond();

            List<String> items = new ArrayList<>();
            for (Holder<Item> holder : content) {
                items.add(holder.unwrapKey().map(k -> k.location().toString()).orElse(""));
            }
            tagMap.put(tag.location().toString(), items);
        });

        FileManager.writeFileOnWorld("tags.json", tagMap, source.getServer());
    }


}


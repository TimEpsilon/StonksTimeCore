package com.github.timepsilon.commands.equivalency;

import com.github.timepsilon.datamaps.DataMaps;
import com.github.timepsilon.datamaps.SCTMap;
import com.github.timepsilon.utils.FileManager;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class GenerateEquivalency {


    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {

        LiteralArgumentBuilder<CommandSourceStack> literalargumentbuilder = Commands.literal("equivalency").requires(p -> p.hasPermission(2));

        literalargumentbuilder
                .executes(context -> {
                            GenerateEquivalency.generateFiles(context);
                            return Command.SINGLE_SUCCESS;
                        }
                );
        dispatcher.register(literalargumentbuilder);
    }

    private static void generateFiles(CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack source = ctx.getSource();
        iterateItems(source);
        iterateRecipes(source);
        source.sendSystemMessage(Component.literal("Item list and Recipe dict have been saved in the config folder").withStyle(ChatFormatting.DARK_GRAY));
    }

    private static void iterateItems(CommandSourceStack source) {
        ArrayList<String> items = new ArrayList<>();
        for (ResourceLocation itemId : BuiltInRegistries.ITEM.keySet()) {
            items.add(itemId.toString());

            SCTMap sct = BuiltInRegistries.ITEM.get(itemId).builtInRegistryHolder().getData(DataMaps.SCT_MAP);
            if (sct != null) {
                System.out.println(sct + " " + sct.SCT());
            }
        }
        FileManager.writeConfigServerSide("items.txt", items, source.getServer());
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

        FileManager.writeConfigServerSide("recipes.json", recipeMap, source.getServer());

    }


}


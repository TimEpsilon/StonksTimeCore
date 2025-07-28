package com.github.timepsilon.server.commands.equivalency.io.minecraft;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;

import java.util.HashMap;

public class RecipeOutputDefault {

    public static HashMap<String, Integer> getOutputs(Recipe<?> recipe, HolderLookup.Provider provider) {
        switch (recipe) {
            default -> {return getOutputDefault(recipe, provider);}
        }
    }

    private static HashMap<String, Integer> getOutputDefault(Recipe<?> recipe, HolderLookup.Provider provider) {
        HashMap<String, Integer> outputMap = new HashMap<>();

        // By default, a recipe returns a single item with a quantity
        ItemStack output = recipe.getResultItem(provider);
        if (!output.isEmpty()) {
            outputMap.put(output.getItem().toString(), output.getCount());
        }
        return outputMap;
    }

}

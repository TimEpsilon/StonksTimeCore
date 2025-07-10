package com.github.timepsilon.server.utils;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.SmithingTransformRecipe;
import net.minecraft.world.item.crafting.SmithingTrimRecipe;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;

public class RecipeInputManager {

    public static HashMap<String, HashMap<String, Integer>> getInputs(Recipe<?> recipe) {
        switch (recipe) {
            case SmithingTransformRecipe smithing -> {return getInputsSmithing(smithing);}
            case SmithingTrimRecipe trim -> {return getInputsTrim(trim);}
            default -> {return getInputsDefault(recipe);}
        }
    }

    private static HashMap<String, Integer> singleItemMap(Ingredient ingredient) {
        HashMap<String, Integer> map = new HashMap<>();
        for (ItemStack stack : ingredient.getItems()) {
            map.merge(stack.getItem().toString(), stack.getCount(), Integer::sum);
        }
        return map;
    }

    private static HashMap<String, HashMap<String, Integer>> getInputsDefault(Recipe<?> recipe) {
        List<Ingredient> ingredients = recipe.getIngredients();
        HashMap<String, HashMap<String, Integer>> inputIngredientMap = new HashMap<>();

        // Since multiple objects can be within a single ingredient, we need to pass this info to the final JSON
        // The goal is to prevent naive propagation of the SCT values within the same ingredient
        for (Ingredient ingredient : ingredients) {
            HashMap<String, Integer> inputMap = singleItemMap(ingredient);
            inputIngredientMap.put(ingredient.toString(), inputMap);
        }
        return inputIngredientMap;
    }

    private static HashMap<String, HashMap<String, Integer>> getInputsSmithing(SmithingTransformRecipe recipe) {
        HashMap<String, HashMap<String, Integer>> inputIngredientMap = new HashMap<>();

        Field f = recipe.getClass().getDeclaredField("template");
        f.setAccessible(True)
        recipe.template

    }

}

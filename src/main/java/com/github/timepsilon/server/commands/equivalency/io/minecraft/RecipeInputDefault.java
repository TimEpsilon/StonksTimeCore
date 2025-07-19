package com.github.timepsilon.server.commands.equivalency.io.minecraft;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;

import java.util.HashMap;
import java.util.List;


public class RecipeInputDefault {

    public static HashMap<String, HashMap<String, Integer>> getInputs(Recipe<?> recipe) {

        switch (recipe) {
            case SmithingTransformRecipe smithing -> {return getInputsSmithing(smithing);}
            case SmithingTrimRecipe trim -> {return getInputsSmithing(trim);}
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

    private static HashMap<String, HashMap<String, Integer>> getInputsSmithing(SmithingRecipe recipe) {
        HashMap<String, HashMap<String, Integer>> inputIngredientMap = new HashMap<>();

        Ingredient template;
        Ingredient addition;
        Ingredient base;

        if (recipe instanceof SmithingTransformRecipe transform) {
            template = transform.template;
            addition = transform.addition;
            base = transform.base;
        } else if (recipe instanceof SmithingTrimRecipe trim) {
            template = trim.template;
            addition = trim.addition;
            base = trim.base;
        } else {
            return inputIngredientMap;
        }

        HashMap<String, Integer> templateMap = singleItemMap(template);
        inputIngredientMap.put(template.toString(), templateMap);

        HashMap<String, Integer> additionMap = singleItemMap(addition);
        inputIngredientMap.put(addition.toString(), additionMap);

        HashMap<String, Integer> baseMap = singleItemMap(base);
        inputIngredientMap.put(base.toString(), baseMap);

        return inputIngredientMap;
    }
}

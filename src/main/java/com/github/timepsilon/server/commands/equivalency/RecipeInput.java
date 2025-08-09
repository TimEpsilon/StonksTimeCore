package com.github.timepsilon.server.commands.equivalency;

import com.simibubi.create.content.kinetics.mixer.CompactingRecipe;
import com.simibubi.create.content.kinetics.mixer.MixingRecipe;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipe;
import com.simibubi.create.content.processing.sequenced.SequencedRecipe;
import com.simibubi.create.foundation.fluid.FluidIngredient;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class RecipeInput {

    public static HashMap<String, HashMap<String, Integer>> getInputs(Recipe<?> recipe) {

        switch (recipe) {
            // Default
            case SmithingTransformRecipe smithing -> {return getInputsSmithing(smithing);}
            case SmithingTrimRecipe trim -> {return getInputsSmithing(trim);}
            // Create
            case MixingRecipe mixing -> {return getInputsBasin(mixing);}
            case CompactingRecipe compacting -> {return getInputsBasin(compacting);}
            case SequencedAssemblyRecipe assembly -> {return getInputsAssembly(assembly);}
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

    private static HashMap<String, Integer> singleFluidMap(FluidIngredient ingredient) {
        HashMap<String, Integer> map = new HashMap<>();
        for (FluidStack fluid : ingredient.getMatchingFluidStacks()) {
            map.merge(fluid.getFluid().toString(), fluid.getAmount(), Integer::sum);
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

    private static HashMap<String, HashMap<String, Integer>> getInputsBasin(BasinRecipe recipe) {
        HashMap<String, HashMap<String, Integer>> inputIngredientMap = getInputsDefault(recipe);

        for (FluidIngredient ingredient : recipe.getFluidIngredients()) {
            HashMap<String, Integer> inputMap = singleFluidMap(ingredient);
            inputIngredientMap.put(ingredient.toString(), inputMap);
        }

        return inputIngredientMap;
    }

    private static HashMap<String, HashMap<String, Integer>> getInputsAssembly(SequencedAssemblyRecipe recipe) {
        ArrayList<Item> tempOutputs = new ArrayList<>();
        int coeff = recipe.getLoops();

        for (SequencedRecipe<?> sequenced : recipe.getSequence()) {
            // A sequenced assembly recipe is a series of recipe where the output of one feeds into the next
            // To take this into account, we need a custom logic that ignores an item if it is the output of another subrecipe
            tempOutputs.add(sequenced.getRecipe().getRollableResults().getFirst().getStack().getItem());
        }

        HashMap<String, HashMap<String, Integer>> inputIngredientMap = getInputsDefault(recipe);
        for (SequencedRecipe<?> sequenced : recipe.getSequence()) {
            for (Ingredient ingredient : sequenced.getRecipe().getIngredients()) {
                HashMap<String, Integer> inputMap = new HashMap<>();

                boolean isThisStartingIngredient = false;
                for (ItemStack stack : ingredient.getItems()) {
                    // If the item is in the temp output list, ignore
                    if (tempOutputs.contains(stack.getItem())) {
                        isThisStartingIngredient = true;
                        continue;
                    }

                    inputMap.merge(stack.getItem().toString(), stack.getCount() * coeff, Integer::sum);
                }

                // If the ingredient contains a temp output and is not empty, the other items are the initial inputs
                // This means that we don't multiply by the amount of loops the items in this ingredient
                if (isThisStartingIngredient) {
                    for (Map.Entry<String, Integer> entry : inputMap.entrySet()) {
                        entry.setValue(entry.getValue() / coeff);
                    }
                }

                // Merge inner ingredient maps
                if (!inputMap.isEmpty()) {
                    inputIngredientMap.merge(
                            ingredient.toString(),
                            inputMap,
                            (existingMap, newMap) -> {
                                newMap.forEach((key, value) ->
                                        existingMap.merge(key, value, Integer::sum)
                                );
                                return existingMap;
                            }
                    );
                }
            }

            // Fluid ingredients
            // Fluids aren't products of a recipe so this works the same as other fluid related crafts
            for (FluidIngredient ingredient : sequenced.getRecipe().getFluidIngredients()) {
                HashMap<String, Integer> inputMap = singleFluidMap(ingredient);

                // Multiply by loop amount
                for (Map.Entry<String, Integer> entry : inputMap.entrySet()) {
                    entry.setValue(entry.getValue() * coeff);
                }

                inputIngredientMap.put(ingredient.toString(), inputMap);
            }
        }
        return inputIngredientMap;
    }
}

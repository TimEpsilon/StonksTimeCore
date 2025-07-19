package com.github.timepsilon.server.commands.equivalency.io.minecraft;

import com.simibubi.create.content.kinetics.mixer.CompactingRecipe;
import com.simibubi.create.content.kinetics.mixer.MixingRecipe;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.HashMap;

public class RecipeOutputDefault {

    public static HashMap<String, Integer> getOutputs(Recipe<?> recipe, HolderLookup.Provider provider) {
        switch (recipe) {
            case MixingRecipe mixing -> {return getOutputsBasin(mixing, provider);}
            case CompactingRecipe compacting -> {return getOutputsBasin(compacting, provider);}
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

    private static HashMap<String, Integer> getOutputsBasin(BasinRecipe recipe,  HolderLookup.Provider provider) {
        HashMap<String, Integer> outputMap = getOutputDefault(recipe, provider);

        for (FluidStack fluid : recipe.getFluidResults()) {
            outputMap.merge(fluid.getFluid().toString(), fluid.getAmount(), Integer::sum);
        }
        return outputMap;
    }

}

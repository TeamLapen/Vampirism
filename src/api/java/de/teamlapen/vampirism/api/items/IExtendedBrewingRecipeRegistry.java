package de.teamlapen.vampirism.api.items;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import org.apache.commons.lang3.tuple.Triple;

import java.util.List;
import java.util.Optional;

public interface IExtendedBrewingRecipeRegistry {

    void addMix(MixPredicate mixPredicate);

    void addMix(MixPredicate[] mixPredicate);

    void addRecipe(IExtendedBrewingRecipe recipe);

    boolean brewPotions(NonNullList<ItemStack> inputs, ItemStack ingredient, ItemStack extraIngredient, IExtendedBrewingCapabilities capabilities, int[] inputIndexes, boolean onlyExtended);

    boolean canBrew(NonNullList<ItemStack> inputs, ItemStack ingredient, ItemStack extraIngredient, IExtendedBrewingCapabilities capabilities, int[] inputIndexes);

    List<MixPredicate> getConversionMixes();

    Optional<Triple<ItemStack, Integer, Integer>> getOutput(ItemStack bottle, ItemStack ingredient, ItemStack extraIngredient, IExtendedBrewingCapabilities capabilities, boolean onlyExtended);

    List<IExtendedBrewingRecipe> getRecipes();

    boolean hasOutput(ItemStack input, ItemStack ingredient, ItemStack extraIngredient, IExtendedBrewingCapabilities capabilities);

    boolean isValidExtraIngredient(ItemStack stack);

    boolean isValidIngredient(ItemStack stack);

    boolean isValidInput(ItemStack stack);

    interface IExtendedBrewingCapabilities {
        boolean isConcentratedBrewing();

        boolean isDurableBrewing();

        boolean isEfficientBrewing();

        boolean isMasterBrewing();

        boolean isMultiTaskBrewing();

        boolean isSwiftBrewing();
    }

}

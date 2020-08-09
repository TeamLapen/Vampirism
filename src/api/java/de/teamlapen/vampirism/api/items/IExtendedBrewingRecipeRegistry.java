package de.teamlapen.vampirism.api.items;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import org.apache.commons.lang3.tuple.Triple;

import java.util.List;
import java.util.Optional;

public interface IExtendedBrewingRecipeRegistry {

    void addMix(ExtendedPotionMix potionMix);

    void addMix(ExtendedPotionMix[] mixPredicate);

    boolean brewPotions(NonNullList<ItemStack> inputs, ItemStack ingredient, ItemStack extraIngredient, IExtendedBrewingCapabilities capabilities, int[] inputIndexes, boolean onlyExtended);

    boolean canBrew(NonNullList<ItemStack> inputs, ItemStack ingredient, ItemStack extraIngredient, IExtendedBrewingCapabilities capabilities, int[] inputIndexes);

    List<ExtendedPotionMix> getPotionMixes();

    Optional<Triple<ItemStack, Integer, Integer>> getOutput(ItemStack bottle, ItemStack ingredient, ItemStack extraIngredient, IExtendedBrewingCapabilities capabilities, boolean onlyExtended);

    boolean hasOutput(ItemStack input, ItemStack ingredient, ItemStack extraIngredient, IExtendedBrewingCapabilities capabilities);

    boolean isValidExtraIngredient(ItemStack stack);

    boolean isValidIngredient(ItemStack stack);

    boolean isValidInput(ItemStack stack);

    interface IExtendedBrewingCapabilities {
        default boolean isConcentratedBrewing() {
            return false;
        }

        default boolean isDurableBrewing() {
            return false;
        }

        default boolean isEfficientBrewing() {
            return false;
        }

        default boolean isMasterBrewing() {
            return false;
        }

        default boolean isMultiTaskBrewing() {
            return false;
        }

        default boolean isSwiftBrewing() {
            return false;
        }
    }

}

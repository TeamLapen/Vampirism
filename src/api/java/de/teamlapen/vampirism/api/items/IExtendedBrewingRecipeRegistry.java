package de.teamlapen.vampirism.api.items;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.tuple.Triple;

import java.util.List;
import java.util.Optional;

public interface IExtendedBrewingRecipeRegistry {

    void addMix(ExtendedPotionMix potionMix);

    void addMix(ExtendedPotionMix[] mixPredicate);

    boolean brewPotions(Level level, NonNullList<ItemStack> inputs, ItemStack ingredient, ItemStack extraIngredient, IExtendedBrewingCapabilities capabilities, int[] inputIndexes, boolean onlyExtended);

    boolean canBrew(Level level, NonNullList<ItemStack> inputs, ItemStack ingredient, ItemStack extraIngredient, IExtendedBrewingCapabilities capabilities, int[] inputIndexes);

    Optional<Triple<ItemStack, Integer, Integer>> getOutput(Level level, ItemStack bottle, ItemStack ingredient, ItemStack extraIngredient, IExtendedBrewingCapabilities capabilities, boolean onlyExtended);

    List<ExtendedPotionMix> getPotionMixes();

    boolean hasOutput(Level level, ItemStack input, ItemStack ingredient, ItemStack extraIngredient, IExtendedBrewingCapabilities capabilities);

    /**
     * Ignores stack size
     *
     * @return Whether the given stack is a valid extra ingredient (Vampirism's extra ingredient) to any recipe.
     */
    boolean isValidExtraIngredient(ItemStack stack);

    /**
     * Ignores stack size
     * @return Whether the given stack is a valid ingredient (vanilla potion ingredient such as gunpowder) to any recipe.
     */
    boolean isValidIngredient(PotionBrewing registry, ItemStack stack);

    /**
     * Ignores stack size
     * @return Whether the given stack is a valid input (potion slot) (to any recipe).
     */
    boolean isValidInput(PotionBrewing registry, ItemStack stack);

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

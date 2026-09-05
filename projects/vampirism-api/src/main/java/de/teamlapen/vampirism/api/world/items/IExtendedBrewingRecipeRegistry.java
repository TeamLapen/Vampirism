package de.teamlapen.vampirism.api.world.items;

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

    /**
     *
     * @param inputs       List with inputs at indices specified by inputIndexes
     * @param inputIndexes Which positions of the inputs list should be tested
     * @return Whether at least one of the given inputs has an output considering the ingredients
     */
    boolean canBrew(Level level, NonNullList<ItemStack> inputs, ItemStack ingredient, ItemStack extraIngredient, IExtendedBrewingCapabilities capabilities, int[] inputIndexes);

    Optional<Triple<ItemStack, Integer, Integer>> getOutput(Level level, ItemStack bottle, ItemStack ingredient, ItemStack extraIngredient, IExtendedBrewingCapabilities capabilities, boolean onlyExtended);

    List<ExtendedPotionMix> getPotionMixes();

    /**
     * @return Whether the given input yields an output
     */
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
        default boolean hasConcentratedBrewing() {
            return false;
        }

        default boolean hasDurableBrewing() {
            return false;
        }

        default boolean hasEfficientBrewing() {
            return false;
        }

        default boolean hasMasterBrewing() {
            return false;
        }

        default boolean hasMultiTaskBrewing() {
            return false;
        }

        default boolean hasSwiftBrewing() {
            return false;
        }

        default boolean hasUltimateBrewing() {
            return false;
        }
    }

}

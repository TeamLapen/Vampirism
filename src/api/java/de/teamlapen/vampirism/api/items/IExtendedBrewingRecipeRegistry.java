package de.teamlapen.vampirism.api.items;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.Potion;
import net.minecraft.util.NonNullList;
import org.apache.commons.lang3.tuple.Triple;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public interface IExtendedBrewingRecipeRegistry {

    void addMix(MixPredicate mixPredicate);

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

    class MixPredicate {
        public final net.minecraftforge.registries.IRegistryDelegate<Potion> input;
        public final Ingredient reagent1;
        public final int reagent1Count;
        public final Ingredient reagent2;
        public final int reagent2Count;
        public final net.minecraftforge.registries.IRegistryDelegate<Potion> output;
        public final Predicate<IExtendedBrewingCapabilities> condition;

        public MixPredicate(Potion inputIn, Ingredient reagentIn1, int count1, Ingredient reagentIn2, int count2, Potion outputIn, Predicate<IExtendedBrewingRecipeRegistry.IExtendedBrewingCapabilities> condition) {
            this.input = inputIn.delegate;
            this.reagent1 = reagentIn1;
            this.reagent1Count = count1;
            this.reagent2 = reagentIn2;
            this.reagent2Count = count2;
            this.output = outputIn.delegate;
            this.condition = condition;
        }

        public MixPredicate(Potion inputIn, Ingredient reagentIn1, Ingredient reagentIn2, Potion outputIn, Predicate<IExtendedBrewingRecipeRegistry.IExtendedBrewingCapabilities> condition) {
            this(inputIn, reagentIn1, reagentIn1 == Ingredient.EMPTY ? 0 : 1, reagentIn2, reagentIn2 == Ingredient.EMPTY ? 0 : 1, outputIn, condition);
        }
    }

}

package de.teamlapen.vampirism.inventory.recipes;

import de.teamlapen.vampirism.api.items.IExtendedBrewingRecipe;
import de.teamlapen.vampirism.api.items.IExtendedBrewingRecipeRegistry;
import de.teamlapen.vampirism.api.items.MixPredicate;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import org.apache.commons.lang3.tuple.Triple;

import java.util.*;


public class ExtendedBrewingRecipeRegistry implements IExtendedBrewingRecipeRegistry {

    private final List<IExtendedBrewingRecipe> recipes = new ArrayList<>();
    private final List<MixPredicate> conversionMixes = new ArrayList<>();


    public ExtendedBrewingRecipeRegistry() {
        this.recipes.add(new ExtendedReagentRecipes());
    }

    @Override
    public void addMix(MixPredicate mixPredicate) {
        this.conversionMixes.add(mixPredicate);
    }

    @Override
    public void addMix(MixPredicate[] mixPredicate) {
        this.conversionMixes.addAll(Arrays.asList(mixPredicate));
    }

    @Override
    public void addRecipe(IExtendedBrewingRecipe recipe) {
        this.recipes.add(recipe);
    }

    @Override
    public boolean brewPotions(NonNullList<ItemStack> inputs, ItemStack ingredient, ItemStack extraIngredient, IExtendedBrewingCapabilities capabilities, int[] inputIndexes, boolean onlyExtended) {
        boolean brewed = false;
        int useMain = 0;
        int useExtra = 0;
        for (int i : inputIndexes) {
            Optional<Triple<ItemStack, Integer, Integer>> output = getOutput(inputs.get(i), ingredient, extraIngredient, capabilities, onlyExtended);
            if (output.isPresent()) {
                Triple<ItemStack, Integer, Integer> triple = output.get();
                inputs.set(i, triple.getLeft());
                useMain = Math.max(useMain, triple.getMiddle());
                useExtra = Math.max(useExtra, triple.getRight());
                brewed = true;
            }
        }
        ingredient.shrink(useMain);
        extraIngredient.shrink(useExtra);
        return brewed;
    }

    @Override
    public boolean canBrew(NonNullList<ItemStack> inputs, ItemStack ingredient, ItemStack extraIngredient, IExtendedBrewingCapabilities capabilities, int[] inputIndexes) {
        if (ingredient.isEmpty()) return false;

        for (int i : inputIndexes) {
            if (hasOutput(inputs.get(i), ingredient, extraIngredient, capabilities)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public List<MixPredicate> getConversionMixes() {
        return Collections.unmodifiableList(conversionMixes);
    }

    @Override
    public Optional<Triple<ItemStack, Integer, Integer>> getOutput(ItemStack bottle, ItemStack ingredient, ItemStack extraIngredient, IExtendedBrewingCapabilities capabilities, boolean onlyExtended) {
        if (bottle.isEmpty() || bottle.getCount() != 1) return Optional.empty();
        if (ingredient.isEmpty()) return Optional.empty();

        for (IExtendedBrewingRecipe recipe : recipes) {
            Optional<Triple<ItemStack, Integer, Integer>> output = recipe.getOutput(bottle, ingredient, extraIngredient, capabilities);
            if (output.isPresent()) {
                return output;
            }
        }
        ItemStack output = BrewingRecipeRegistry.getOutput(bottle, ingredient);
        return output.isEmpty() ? Optional.empty() : Optional.of(Triple.of(output, 1, 0));
    }

    @Override
    public List<IExtendedBrewingRecipe> getRecipes() {
        return Collections.unmodifiableList(recipes);
    }

    @Override
    public boolean hasOutput(ItemStack input, ItemStack ingredient, ItemStack extraIngredient, IExtendedBrewingCapabilities capabilities) {
        return getOutput(input, ingredient, extraIngredient, capabilities, false).isPresent();
    }

    @Override
    public boolean isValidExtraIngredient(ItemStack stack) {
        if (stack.isEmpty()) return false;

        for (IExtendedBrewingRecipe recipe : recipes) {
            if (recipe.isExtraIngredient(stack)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isValidIngredient(ItemStack stack) {
        if (stack.isEmpty()) return false;

        for (IExtendedBrewingRecipe recipe : recipes) {
            if (recipe.isIngredient(stack)) {
                return true;
            }
        }
        return BrewingRecipeRegistry.isValidIngredient(stack);
    }

    @Override
    public boolean isValidInput(ItemStack stack) {
        if (stack.getCount() != 1) return false;

        for (IExtendedBrewingRecipe recipe : recipes) {
            if (recipe.isInput(stack)) {
                return true;
            }
        }
        return BrewingRecipeRegistry.isValidIngredient(stack);
    }
}

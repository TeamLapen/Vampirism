package de.teamlapen.vampirism.recipes;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractBrewingRecipe implements Recipe<BrewingRecipeInput> {

    protected final RecipeType<?> type;
    protected final String group;
    protected final Ingredient ingredient;
    protected final Ingredient input;
    protected final ItemStack result;

    public AbstractBrewingRecipe(RecipeType<?> type, String group, Ingredient ingredient, Ingredient input, ItemStack result) {
        this.type = type;
        this.group = group;
        this.ingredient = ingredient;
        this.input = input;
        this.result = result;
    }

    @Override
    public boolean matches(@NotNull BrewingRecipeInput inventory, @NotNull Level level) {
        return this.ingredient.test(inventory.input());
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public Ingredient getInput() {
        return input;
    }

    @NotNull
    @Override
    public NonNullList<Ingredient> getIngredients() {
        return NonNullList.of(Ingredient.EMPTY, this.ingredient);
    }

    @NotNull
    @Override
    public ItemStack assemble(@NotNull BrewingRecipeInput inventory, HolderLookup.@NotNull Provider lookupProvider) {
        return this.result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int xSize, int ySize) {
        return true;
    }

    @NotNull
    @Override
    public ItemStack getResultItem(HolderLookup.@NotNull Provider lookupProvider) {
        return this.result;
    }

    @NotNull
    @Override
    public RecipeType<?> getType() {
        return this.type;
    }
}

package de.teamlapen.vampirism.common.world.items.recipes;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

public abstract class AbstractBrewingRecipe implements Recipe<BrewingRecipeInput> {

    protected final RecipeType<? extends AbstractBrewingRecipe> type;
    protected final CommonInfo commonInfo;
    protected final String group;
    protected final Ingredient ingredient;
    protected final Ingredient input;
    protected final ItemStack result;

    public AbstractBrewingRecipe(RecipeType<? extends AbstractBrewingRecipe> type, CommonInfo commonInfo, String group, Ingredient ingredient, Ingredient input, ItemStack result) {
        this.type = type;
        this.commonInfo = commonInfo;
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

    public ItemStack getResultItem() {
        return result;
    }

    @Override
    public @NonNull String group() {
        return this.group;
    }

    @Override
    public boolean showNotification() {
        return this.commonInfo.showNotification();
    }

    @NotNull
    @Override
    public ItemStack assemble(@NotNull BrewingRecipeInput inventory) {
        return this.result.copy();
    }

    @Override
    public @NotNull RecipeType<? extends Recipe<BrewingRecipeInput>> getType() {
        return this.type;
    }
}

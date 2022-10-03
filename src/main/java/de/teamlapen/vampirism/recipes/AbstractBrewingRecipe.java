package de.teamlapen.vampirism.recipes;

import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractBrewingRecipe implements Recipe<Container> {

    protected final RecipeType<?> type;
    protected final ResourceLocation id;
    protected final String group;
    protected final Ingredient ingredient;
    protected final Ingredient input;
    protected final ItemStack result;

    public AbstractBrewingRecipe(RecipeType<?> type, ResourceLocation id, String group, Ingredient ingredient, Ingredient input, ItemStack result) {
        this.type = type;
        this.id = id;
        this.group = group;
        this.ingredient = ingredient;
        this.input = input;
        this.result = result;
    }

    @Override
    public boolean matches(@NotNull Container inventory, @NotNull Level level) {
        return this.ingredient.test(inventory.getItem(0));
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
    public ItemStack assemble(@NotNull Container inventory) {
        return this.result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int xSize, int ySize) {
        return true;
    }

    @NotNull
    @Override
    public ItemStack getResultItem() {
        return this.result;
    }

    @NotNull
    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @NotNull
    @Override
    public RecipeType<?> getType() {
        return this.type;
    }
}

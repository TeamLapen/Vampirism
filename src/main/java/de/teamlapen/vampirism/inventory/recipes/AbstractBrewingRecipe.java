package de.teamlapen.vampirism.inventory.recipes;

import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;

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
    public boolean matches(Container inventory, @Nonnull Level level) {
        return this.ingredient.test(inventory.getItem(0));
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public Ingredient getInput() {
        return input;
    }

    @Nonnull
    @Override
    public NonNullList<Ingredient> getIngredients() {
        return NonNullList.of(Ingredient.EMPTY, this.ingredient);
    }

    @Nonnull
    @Override
    public ItemStack assemble(@Nonnull Container inventory) {
        return this.result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int xSize, int ySize) {
        return true;
    }

    @Nonnull
    @Override
    public ItemStack getResultItem() {
        return this.result;
    }

    @Nonnull
    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Nonnull
    @Override
    public RecipeType<?> getType() {
        return this.type;
    }
}

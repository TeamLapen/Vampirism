package de.teamlapen.vampirism.inventory.recipes;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public abstract class AbstractBrewingRecipe implements IRecipe<IInventory> {

    protected final IRecipeType<?> type;
    protected final ResourceLocation id;
    protected final String group;
    protected final Ingredient ingredient;
    protected final Ingredient input;
    protected final ItemStack result;

    public AbstractBrewingRecipe(IRecipeType<?> type, ResourceLocation id, String group, Ingredient ingredient, Ingredient input, ItemStack result) {
        this.type = type;
        this.id = id;
        this.group = group;
        this.ingredient = ingredient;
        this.input = input;
        this.result = result;
    }

    @Override
    public boolean matches(IInventory inventory, @Nonnull World level) {
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
    public ItemStack assemble(@Nonnull IInventory inventory) {
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
    public IRecipeType<?> getType() {
        return this.type;
    }
}

package de.teamlapen.vampirism.inventory.recipes;

import com.mojang.datafixers.util.Either;
import de.teamlapen.lib.lib.util.UtilLib;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ITag;
import net.minecraftforge.common.brewing.IBrewingRecipe;

import javax.annotation.Nonnull;

public class TagNBTBrewingRecipe implements IBrewingRecipe {

    private final Ingredient input;
    private final Either<ITag<Item>, Ingredient> ingredient;
    private final ItemStack output;

    public TagNBTBrewingRecipe(Ingredient input, ITag<Item> ingredient, ItemStack output) {
        this.input = input;
        this.ingredient = Either.left(ingredient);
        this.output = output;
    }

    public TagNBTBrewingRecipe(Ingredient input, Ingredient ingredient, ItemStack output) {
        this.input = input;
        this.ingredient = Either.right(ingredient);
        this.output = output;
    }

    @Override
    public boolean isInput(@Nonnull ItemStack input) {
        return UtilLib.matchesItem(this.input, input);
    }

    @Override
    public boolean isIngredient(@Nonnull ItemStack ingredient) {
        return this.ingredient.map(i -> i.contains(ingredient.getItem()), i -> i.test(ingredient));
    }

    @Nonnull
    @Override
    public ItemStack getOutput(@Nonnull ItemStack input, @Nonnull ItemStack ingredient) {
        return isInput(input) && isIngredient(ingredient) ? this.output.copy() : ItemStack.EMPTY;
    }

    public ItemStack[] getIngredient() {
        return ingredient.map(i -> i.getValues().stream().map(ItemStack::new).toArray(ItemStack[]::new), Ingredient::getItems);
    }

    public Ingredient getInput() {
        return input;
    }

    public ItemStack getOutput() {
        return output;
    }
}

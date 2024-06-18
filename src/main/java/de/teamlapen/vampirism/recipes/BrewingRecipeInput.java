package de.teamlapen.vampirism.recipes;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeInput;
import org.jetbrains.annotations.NotNull;

public record BrewingRecipeInput(ItemStack input, ItemStack ingredient) implements RecipeInput {

    @Override
    public @NotNull ItemStack getItem(int p_346128_) {
        return switch (p_346128_) {
            case 0 -> this.input;
            case 1 -> this.ingredient;
            default -> throw new IllegalArgumentException("Recipe does not contain slot " + p_346128_);
        };
    }

    @Override
    public int size() {
        return 2;
    }
}

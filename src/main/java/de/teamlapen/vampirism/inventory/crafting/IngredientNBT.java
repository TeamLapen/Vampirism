package de.teamlapen.vampirism.inventory.crafting;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;

import javax.annotation.Nullable;

public class IngredientNBT extends Ingredient { //TODO does we need this class?
    private final ItemStack stack;

    public IngredientNBT(ItemStack stack) {
        super(stack);
        this.stack = stack;
    }

    @Override
    public boolean apply(@Nullable ItemStack input) {
        return input != null && ItemStack.areItemStacksEqualUsingNBTShareTag(this.stack, input);
    }
}
package de.teamlapen.vampirism.api.items;

import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.tuple.Triple;

import java.util.Optional;


public interface IExtendedBrewingRecipe {

    Optional<Triple<ItemStack, Integer, Integer>> getOutput(ItemStack bottleItem, ItemStack ingredient, ItemStack extraIngredient, IExtendedBrewingRecipeRegistry.IExtendedBrewingCapabilities capabilities);

    boolean isExtraIngredient(ItemStack extraIngredient);

    boolean isIngredient(ItemStack ingredient);

    boolean isInput(ItemStack bottleItem);
}

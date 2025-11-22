package de.teamlapen.vampirism.misc.extension;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.ShapedRecipePattern;

public interface IShapedRecipe {

    ItemStack getResult();

    ShapedRecipePattern getPattern();
}

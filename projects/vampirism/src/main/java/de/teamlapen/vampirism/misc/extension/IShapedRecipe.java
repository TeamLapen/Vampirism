package de.teamlapen.vampirism.misc.extension;

import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.ShapedRecipePattern;

public interface IShapedRecipe {

    ItemStackTemplate getResult();

    ShapedRecipePattern getPattern();
}

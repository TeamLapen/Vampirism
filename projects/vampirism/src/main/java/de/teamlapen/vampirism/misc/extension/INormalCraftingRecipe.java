package de.teamlapen.vampirism.misc.extension;

import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;

public interface INormalCraftingRecipe {

    Recipe.CommonInfo getCommonInfo();
    CraftingRecipe.CraftingBookInfo getBookInfo();
}

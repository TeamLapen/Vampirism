package de.teamlapen.vampirism.misc.extension;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeUnlockAdvancementBuilder;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;
import java.util.Map;

public interface IShapedRecipeBuilder {

    RecipeCategory getCategory();

    Map<Character, Ingredient> key();

    List<String> rows();

    RecipeUnlockAdvancementBuilder advancementBuilder();

}

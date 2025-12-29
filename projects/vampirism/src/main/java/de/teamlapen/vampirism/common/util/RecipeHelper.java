package de.teamlapen.vampirism.common.util;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.common.core.ModRecipes;
import de.teamlapen.vampirism.common.world.items.recipes.InfuserRecipe;
import net.minecraft.world.item.crafting.RecipePropertySet;
import net.minecraft.world.level.Level;

import java.util.stream.Stream;

public class RecipeHelper {

    public static RecipePropertySet createLocalInfuserRecipePropertySet(Level level) {
        return RecipePropertySet.create(VampirismMod.services().recipes().getRecipes().byType(ModRecipes.INFUSER_TYPE.get()).stream().flatMap(x ->  {
            InfuserRecipe recipe = x.value();
            return Stream.of(recipe.ingredient1(), recipe.ingredient2(), recipe.ingredient3(), recipe.ingredient4());
        }).toList());
    }

    public static RecipePropertySet createLocalInfuserRecipeInputPropertySet(Level level) {
        return RecipePropertySet.create(VampirismMod.services().recipes().getRecipes().byType(ModRecipes.INFUSER_TYPE.get()).stream().flatMap(x -> Stream.of(x.value().ingredient())).toList());
    }
}

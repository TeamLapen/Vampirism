package de.teamlapen.vampirism.modcompat.jei.recipes.maker;

import de.teamlapen.vampirism.api.VampirismDataMaps;
import de.teamlapen.vampirism.api.datamaps.IGarlicDiffuserFuel;
import de.teamlapen.vampirism.modcompat.jei.recipes.GarlicDiffuserRecipe;
import mezz.jei.api.runtime.IIngredientManager;

import java.util.Comparator;
import java.util.List;

public class GarlicDiffuserRecipeMaker {

    public static List<GarlicDiffuserRecipe> getRecipes(IIngredientManager ingredientManager) {
        return ingredientManager.getAllItemStacks().stream()
                .<GarlicDiffuserRecipe>mapMulti((stack, consumer) -> {
            IGarlicDiffuserFuel data = stack.getItemHolder().getData(VampirismDataMaps.GARLIC_DIFFUSER_FUEL.get());
            if (data != null)  {
                consumer.accept(new GarlicDiffuserRecipe(stack, data));
            }
        }).sorted(Comparator.comparingInt(GarlicDiffuserRecipe::getBurnTime)).toList();
    }
}

package de.teamlapen.vampirism.modcompat.jei.recipes.maker;

import de.teamlapen.vampirism.api.VampirismDataMaps;
import de.teamlapen.vampirism.api.datamaps.IFogDiffuserFuel;
import de.teamlapen.vampirism.modcompat.jei.recipes.FogDiffuserRecipe;
import mezz.jei.api.runtime.IIngredientManager;

import java.util.Comparator;
import java.util.List;

public class FogDiffuserRecipeMaker {

    public static List<FogDiffuserRecipe> getRecipes(IIngredientManager ingredientManager) {
        return ingredientManager.getAllItemStacks().stream()
                .<FogDiffuserRecipe>mapMulti((stack, consumer) -> {
            IFogDiffuserFuel data = stack.getItemHolder().getData(VampirismDataMaps.FOG_DIFFUSER_FUEL.get());
            if (data != null)  {
                consumer.accept(new FogDiffuserRecipe(stack, data));
            }
        }).sorted(Comparator.comparingInt(FogDiffuserRecipe::getBurnTime)).toList();
    }
}

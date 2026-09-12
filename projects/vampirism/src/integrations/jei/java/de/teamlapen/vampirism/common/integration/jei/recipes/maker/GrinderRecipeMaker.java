package de.teamlapen.vampirism.common.integration.jei.recipes.maker;

import de.teamlapen.vampirism.api.datamaps.IItemBlood;
import de.teamlapen.vampirism.common.core.ModDataMaps;
import de.teamlapen.vampirism.common.integration.jei.recipes.GrinderRecipe;
import mezz.jei.api.runtime.IIngredientManager;

import java.util.Comparator;
import java.util.List;

public class GrinderRecipeMaker {

    public static List<GrinderRecipe> getRecipes(IIngredientManager ingredientManager) {
        return ingredientManager.getAllItemStacks().stream()
                .<GrinderRecipe>mapMulti((stack, consumer) -> {
                    IItemBlood data = stack.typeHolder().getData(ModDataMaps.ITEM_BLOOD_MAP);
                    if (data != null && data.blood() > 0) {
                        consumer.accept(new GrinderRecipe(stack, data));
                    }
                }).sorted(Comparator.comparingInt(GrinderRecipe::blood)).toList();
    }
}

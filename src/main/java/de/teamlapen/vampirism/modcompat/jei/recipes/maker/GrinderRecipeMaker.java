package de.teamlapen.vampirism.modcompat.jei.recipes.maker;

import de.teamlapen.vampirism.api.VampirismDataMaps;
import de.teamlapen.vampirism.api.datamaps.IGarlicDiffuserFuel;
import de.teamlapen.vampirism.api.datamaps.IItemBlood;
import de.teamlapen.vampirism.modcompat.jei.recipes.GarlicDiffuserRecipe;
import de.teamlapen.vampirism.modcompat.jei.recipes.GrinderRecipe;
import mezz.jei.api.runtime.IIngredientManager;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.List;

public class GrinderRecipeMaker {

    public static List<GrinderRecipe> getRecipes(IIngredientManager ingredientManager) {
        return ingredientManager.getAllItemStacks().stream()
                .<GrinderRecipe>mapMulti((stack, consumer) -> {
            IItemBlood data = stack.getItemHolder().getData(VampirismDataMaps.ITEM_BLOOD.get());
            if (data != null && data.blood() > 0)  {
                consumer.accept(new GrinderRecipe(stack, data));
            }
        }).sorted(Comparator.comparingInt(GrinderRecipe::blood)).toList();
    }
}

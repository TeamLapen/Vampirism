package de.teamlapen.vampirism.modcompat.jei.recipes.maker;

import de.teamlapen.vampirism.api.VampirismDataMaps;
import de.teamlapen.vampirism.api.datamaps.IFluidBloodConversion;
import de.teamlapen.vampirism.api.datamaps.IItemBlood;
import de.teamlapen.vampirism.modcompat.jei.recipes.BloodSieveRecipe;
import de.teamlapen.vampirism.modcompat.jei.recipes.GrinderRecipe;
import mezz.jei.api.neoforge.NeoForgeTypes;
import mezz.jei.api.runtime.IIngredientManager;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.List;

public class BloodSieveRecipeMaker {

    public static List<BloodSieveRecipe> getRecipes(IIngredientManager ingredientManager) {
        return ingredientManager.getAllIngredients(NeoForgeTypes.FLUID_STACK).stream()
                .<BloodSieveRecipe>mapMulti((stack, consumer) -> {
            IFluidBloodConversion data = stack.getFluidHolder().getData(VampirismDataMaps.FLUID_BLOOD_CONVERSION.get());
            if (data != null && data.conversionRate() > 0)  {
                consumer.accept(new BloodSieveRecipe(stack, data));
            }
        }).sorted(Comparator.comparingDouble(BloodSieveRecipe::conversionRate)).toList();
    }
}

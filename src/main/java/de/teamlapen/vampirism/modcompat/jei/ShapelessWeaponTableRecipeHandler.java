package de.teamlapen.vampirism.modcompat.jei;

import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.recipes.ShapelessWeaponTableRecipe;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;


public class ShapelessWeaponTableRecipeHandler implements IRecipeHandler<ShapelessWeaponTableRecipe> {

    @Nonnull
    @Override
    public String getRecipeCategoryUid(@Nonnull ShapelessWeaponTableRecipe recipe) {
        return VampirismJEIPlugin.HUNTER_WEAPON_RECIPE_UID;
    }

    @Nonnull
    @Override
    public Class<ShapelessWeaponTableRecipe> getRecipeClass() {
        return ShapelessWeaponTableRecipe.class;
    }

    @Nonnull
    @Override
    public IRecipeWrapper getRecipeWrapper(@Nonnull ShapelessWeaponTableRecipe recipe) {
        return new ShapelessWeaponTableRecipeWrapper(recipe);
    }

    @Override
    public boolean isRecipeValid(@Nonnull ShapelessWeaponTableRecipe recipe) {
        int inputCount = 0;
        for (Object input : recipe.recipeItems) {
            if (input instanceof ItemStack && !((ItemStack) input).isEmpty()) {
                inputCount++;
            } else {
                VampirismMod.log.w("JeiCompat", "Recipe has an input that is not an ItemStack. {Output:%s,Inputs:%s}", recipe.getRecipeOutput(), recipe.recipeItems);
                return false;
            }
        }
        if (inputCount > 16) {
            VampirismMod.log.w("JeiCompat", "Recipe has too many inputs. {Output:%s,Inputs:%s}", recipe.getRecipeOutput(), recipe.recipeItems);
            return false;
        }
        return inputCount > 0;
    }
}

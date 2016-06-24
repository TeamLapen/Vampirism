package de.teamlapen.vampirism.modcompat.jei;

import de.teamlapen.vampirism.inventory.ShapedHunterWeaponRecipe;
import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;


public class ShapedHunterWeaponRecipesHandler implements IRecipeHandler<ShapedHunterWeaponRecipe> {
    @Nonnull
    @Override
    public String getRecipeCategoryUid() {
        return VampirismJEIPlugin.HUNTER_WEAPON_RECIPE_UID;
    }

    @Nonnull
    @Override
    public String getRecipeCategoryUid(@Nonnull ShapedHunterWeaponRecipe recipe) {
        return VampirismJEIPlugin.HUNTER_WEAPON_RECIPE_UID;
    }

    @Nonnull
    @Override
    public Class<ShapedHunterWeaponRecipe> getRecipeClass() {
        return ShapedHunterWeaponRecipe.class;
    }

    @Nonnull
    @Override
    public IRecipeWrapper getRecipeWrapper(@Nonnull ShapedHunterWeaponRecipe recipe) {
        return new ShapedHunterWeaponRecipesWrapper(recipe);
    }

    @Override
    public boolean isRecipeValid(@Nonnull ShapedHunterWeaponRecipe recipe) {
        if (recipe.getRecipeOutput() == null) {
            return false;
        }
        int inputCount = 0;
        for (ItemStack input : recipe.recipeItems) {
            if (input != null) {
                inputCount++;
            }
        }
        return inputCount > 0 && inputCount <= 16;
    }
}

package de.teamlapen.vampirism.modcompat.jei;

import de.teamlapen.vampirism.inventory.ShapedHunterWeaponRecipe;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


/**
 * Wraps {@link ShapedHunterWeaponRecipe}. Draws info about the required level and skill as well as an lava bucket icon if lava is required.
 */
public class ShapedHunterWeaponRecipesWrapper extends HunterWeaponRecipeWrapper {

    private
    @Nonnull
    final ShapedHunterWeaponRecipe recipe;

    public ShapedHunterWeaponRecipesWrapper(@Nonnull ShapedHunterWeaponRecipe recipe) {
        super(recipe);
        this.recipe = recipe;
    }





    @Nonnull
    @Override
    public List getInputs() {
        return Arrays.asList(recipe.recipeItems);
    }

    @Nonnull
    @Override
    public List<ItemStack> getOutputs() {
        return Collections.singletonList(recipe.getRecipeOutput());
    }

    public int getWidth() {
        return recipe.recipeWidth;
    }

    public int getHeight() {
        return recipe.recipeHeight;
    }
}

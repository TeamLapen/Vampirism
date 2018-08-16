package de.teamlapen.vampirism.modcompat.jei;

import de.teamlapen.vampirism.inventory.ShapelessHunterWeaponRecipe;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;


public class ShapelessHunterWeaponRecipeWrapper extends HunterWeaponRecipeWrapper {
    private final
    @Nonnull
    ShapelessHunterWeaponRecipe recipe;

    protected ShapelessHunterWeaponRecipeWrapper(@Nonnull ShapelessHunterWeaponRecipe recipe) {
        super(recipe);
        this.recipe = recipe;
        for (Object input : this.recipe.recipeItems) {
            if (input instanceof ItemStack) {
                ItemStack itemStack = (ItemStack) input;
                if (itemStack.getCount() != 1) {
                    itemStack.setCount(1);
                }
            }
        }
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        super.getIngredients(ingredients);
        ingredients.setInputs(ItemStack.class, recipe.recipeItems);
        ItemStack recipeOutput = recipe.getRecipeOutput();
        if (!recipeOutput.isEmpty()) {
            ingredients.setOutput(ItemStack.class, recipeOutput);
        }
    }

}

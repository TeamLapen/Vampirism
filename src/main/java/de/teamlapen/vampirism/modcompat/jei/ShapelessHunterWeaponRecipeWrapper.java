package de.teamlapen.vampirism.modcompat.jei;

import com.google.common.collect.Lists;
import de.teamlapen.vampirism.inventory.ShapelessHunterWeaponRecipe;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.List;


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
                if (itemStack.stackSize != 1) {
                    itemStack.stackSize = 1;
                }
            }
        }
    }

    @Nonnull
    @Override
    public List getInputs() {
        return Lists.newArrayList(recipe.recipeItems);
    }

    @Nonnull
    @Override
    public List getOutputs() {
        return super.getOutputs();
    }
}

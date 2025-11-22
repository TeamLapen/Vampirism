package de.teamlapen.vampirism.misc.injection;

import de.teamlapen.vampirism.misc.extension.IShapedRecipe;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.ShapedRecipePattern;

public interface IShapedRecipeMock extends IShapedRecipe {
    @Override
    default ItemStack getResult() {
        return null;
    }

    @Override
    default ShapedRecipePattern getPattern() {
        return null;
    }
}

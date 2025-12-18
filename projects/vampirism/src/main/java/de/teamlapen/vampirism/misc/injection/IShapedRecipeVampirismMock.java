package de.teamlapen.vampirism.misc.injection;

import de.teamlapen.vampirism.misc.extension.IShapedRecipe;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.ShapedRecipePattern;

@Deprecated
public interface IShapedRecipeVampirismMock extends IShapedRecipe {
    @Override
    default ItemStack getResult() {
        throw new IllegalStateException("This class is only supported as injection class");
    }

    @Override
    default ShapedRecipePattern getPattern() {
        throw new IllegalStateException("This class is only supported as injection class");
    }
}

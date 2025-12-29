package de.teamlapen.vampirism.misc.injection;

import de.teamlapen.vampirism.misc.extension.IShapedRecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;

@Deprecated
public interface IShapedRecipeBuilderVampirismMock extends IShapedRecipeBuilder {
    @Override
    default RecipeCategory getCategory() {
        throw new IllegalStateException("This class is only supported as injection class");
    }
}

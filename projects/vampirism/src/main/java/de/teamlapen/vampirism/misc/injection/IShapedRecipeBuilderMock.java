package de.teamlapen.vampirism.misc.injection;

import de.teamlapen.vampirism.misc.extension.IShapedRecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;

public interface IShapedRecipeBuilderMock extends IShapedRecipeBuilder {
    @Override
    default RecipeCategory getCategory() {
        return null;
    }
}

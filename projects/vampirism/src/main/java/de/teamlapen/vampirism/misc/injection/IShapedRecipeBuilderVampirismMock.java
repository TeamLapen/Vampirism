package de.teamlapen.vampirism.misc.injection;

import de.teamlapen.vampirism.misc.extension.IShapedRecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeUnlockAdvancementBuilder;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;
import java.util.Map;

@Deprecated
public interface IShapedRecipeBuilderVampirismMock extends IShapedRecipeBuilder {
    @Override
    default RecipeCategory getCategory() {
        throw new IllegalStateException("This class is only supported as injection class");
    }

    @Override
    default Map<Character, Ingredient> key() {
        throw new IllegalStateException("This class is only supported as injection class");
    }

    @Override
    default List<String> rows() {
        throw new IllegalStateException("This class is only supported as injection class");
    }

    @Override
    default RecipeUnlockAdvancementBuilder advancementBuilder() {
        throw new IllegalStateException("This class is only supported as injection class");
    }
}

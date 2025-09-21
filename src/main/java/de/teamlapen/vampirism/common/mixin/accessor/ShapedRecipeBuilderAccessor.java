package de.teamlapen.vampirism.common.mixin.accessor;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ShapedRecipeBuilder.class)
public interface ShapedRecipeBuilderAccessor {

    @Accessor("category")
    RecipeCategory getCategory();
}

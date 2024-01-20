package de.teamlapen.vampirism.mixin.accessor;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ShapelessRecipeBuilder.class)
public interface ShapelessRecipeBuilderAccessor {
    @Accessor("category")
    RecipeCategory getRecipeCategory();
}

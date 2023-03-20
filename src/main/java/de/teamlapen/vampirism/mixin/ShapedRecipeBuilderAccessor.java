package de.teamlapen.vampirism.mixin;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ShapedRecipeBuilder.class)
public interface ShapedRecipeBuilderAccessor {
    @Accessor("category")
    RecipeCategory getRecipeCategory();

    @Accessor("showNotification")
    boolean getShowNotification();
}

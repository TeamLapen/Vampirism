package de.teamlapen.vampirism.misc.mixin.accessor;

import de.teamlapen.vampirism.misc.extension.IShapedRecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeUnlockAdvancementBuilder;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.crafting.Ingredient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;
import java.util.Map;

@Mixin(ShapedRecipeBuilder.class)
public interface ShapedRecipeBuilderAccessor extends IShapedRecipeBuilder {

    @Override
    @Accessor("category")
    RecipeCategory getCategory();

    @Override
    @Accessor("key")
    Map<Character, Ingredient> key();

    @Override
    @Accessor("rows")
    List<String> rows();

    @Override
    @Accessor("advancementBuilder")
    RecipeUnlockAdvancementBuilder advancementBuilder();
}

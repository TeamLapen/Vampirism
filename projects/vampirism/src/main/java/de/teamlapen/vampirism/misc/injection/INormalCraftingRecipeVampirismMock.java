package de.teamlapen.vampirism.misc.injection;

import de.teamlapen.vampirism.misc.extension.INormalCraftingRecipe;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;

@Deprecated
public interface INormalCraftingRecipeVampirismMock extends INormalCraftingRecipe {
    @Override
    default Recipe.CommonInfo getCommonInfo() {
        throw new IllegalStateException("This class is only supported as injection class");
    }

    @Override
    default CraftingRecipe.CraftingBookInfo getBookInfo() {
        throw new IllegalStateException("This class is only supported as injection class");
    }
}

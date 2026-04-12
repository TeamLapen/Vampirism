package de.teamlapen.vampirism.misc.mixin.accessor;

import de.teamlapen.vampirism.misc.extension.INormalCraftingRecipe;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.NormalCraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(NormalCraftingRecipe.class)
public interface NormalCraftingRecipeAccessor extends INormalCraftingRecipe {
    @Override
    @Accessor("commonInfo")
    Recipe.CommonInfo getCommonInfo();

    @Override
    @Accessor("bookInfo")
    CraftingRecipe.CraftingBookInfo getBookInfo();
}

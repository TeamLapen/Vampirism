package de.teamlapen.vampirism.mixin.client;

import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeBookPage;
import net.minecraft.world.entity.player.StackedContents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RecipeBookComponent.class)
public interface RecipeBookComponentAccessor {

    @Accessor("recipeBookPage")
    RecipeBookPage getRecipeBookPage();

    @Accessor("stackedContents")
    StackedContents getStackedContents();
}

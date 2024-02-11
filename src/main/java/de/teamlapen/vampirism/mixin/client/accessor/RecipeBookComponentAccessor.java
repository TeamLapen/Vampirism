package de.teamlapen.vampirism.mixin.client.accessor;

import net.minecraft.client.ClientRecipeBook;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeBookPage;
import net.minecraft.client.gui.screens.recipebook.RecipeBookTabButton;
import net.minecraft.world.entity.player.StackedContents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RecipeBookComponent.class)
public interface RecipeBookComponentAccessor {

    @Accessor("recipeBookPage")
    RecipeBookPage getRecipeBookPage();

    @Accessor("stackedContents")
    StackedContents getStackedContents();

    @Accessor("book")
    ClientRecipeBook getBook();

    @Accessor("selectedTab")
    RecipeBookTabButton getSelectedTab();

    @Accessor("searchBox")
    EditBox getSearchBox();
}

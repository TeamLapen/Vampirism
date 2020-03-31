package de.teamlapen.vampirism.modcompat.guide.recipes;

import amerifrance.guideapi.api.impl.Book;
import amerifrance.guideapi.api.impl.abstraction.CategoryAbstract;
import amerifrance.guideapi.api.impl.abstraction.EntryAbstract;
import amerifrance.guideapi.api.util.GuiHelper;
import amerifrance.guideapi.api.util.IngredientCycler;
import amerifrance.guideapi.gui.BaseScreen;
import de.teamlapen.vampirism.inventory.recipes.ShapedWeaponTableRecipe;
import net.minecraft.client.gui.FontRenderer;

public class ShapedWeaponTableRecipeRenderer extends BasicWeaponTableRecipeRenderer<ShapedWeaponTableRecipe> {
    public ShapedWeaponTableRecipeRenderer(ShapedWeaponTableRecipe recipe) {
        super(recipe);
    }

    @Override
    public void draw(Book book, CategoryAbstract categoryAbstract, EntryAbstract entryAbstract, int guiLeft, int guiTop, int mouseX, int mouseY, BaseScreen baseScreen, FontRenderer fontRenderer, IngredientCycler ingredientCycler) {
        super.draw(book, categoryAbstract, entryAbstract, guiLeft, guiTop, mouseX, mouseY, baseScreen, fontRenderer, ingredientCycler);
        for (int y = 0; y < recipe.getRecipeHeight(); y++) {
            for (int x = 0; x < recipe.getRecipeWidth(); x++) {
                int stackX = (x + 1) * 17 + (guiLeft + 29);
                int stackY = (y + 1) * 17 + (guiTop + 30);
                int i = y * recipe.getRecipeWidth() + x;
                if (i < recipe.getIngredients().size()) {
                    ingredientCycler.getCycledIngredientStack(recipe.getIngredients().get(i), i).ifPresent(stack -> {
                        GuiHelper.drawItemStack(stack, stackX, stackY);
                        if (GuiHelper.isMouseBetween(mouseX, mouseY, stackX, stackY, 15, 15)) {
                            tooltips = GuiHelper.getTooltip(stack);
                        }
                    });
                }
            }
        }
    }
}

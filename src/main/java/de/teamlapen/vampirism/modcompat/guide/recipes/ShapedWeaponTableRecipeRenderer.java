package de.teamlapen.vampirism.modcompat.guide.recipes;

import de.maxanier.guideapi.api.impl.Book;
import de.maxanier.guideapi.api.impl.abstraction.CategoryAbstract;
import de.maxanier.guideapi.api.impl.abstraction.EntryAbstract;
import de.maxanier.guideapi.api.util.GuiHelper;
import de.maxanier.guideapi.api.util.IngredientCycler;
import de.maxanier.guideapi.gui.BaseScreen;
import de.teamlapen.vampirism.recipes.ShapedWeaponTableRecipe;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.RegistryAccess;
import org.jetbrains.annotations.NotNull;

public class ShapedWeaponTableRecipeRenderer extends BasicWeaponTableRecipeRenderer<ShapedWeaponTableRecipe> {
    public ShapedWeaponTableRecipeRenderer(ShapedWeaponTableRecipe recipe) {
        super(recipe);
    }

    @Override
    public void draw(@NotNull GuiGraphics guiGraphics, RegistryAccess registryAccess, Book book, CategoryAbstract categoryAbstract, EntryAbstract entryAbstract, int guiLeft, int guiTop, int mouseX, int mouseY, @NotNull BaseScreen baseScreen, @NotNull Font fontRenderer, @NotNull IngredientCycler ingredientCycler) {
        super.draw(guiGraphics, registryAccess, book, categoryAbstract, entryAbstract, guiLeft, guiTop, mouseX, mouseY, baseScreen, fontRenderer, ingredientCycler);
        for (int y = 0; y < recipe.getRecipeHeight(); y++) {
            for (int x = 0; x < recipe.getRecipeWidth(); x++) {
                int stackX = (x + 1) * 17 + (guiLeft + 49);
                int stackY = (y + 1) * 17 + (guiTop + 30);
                int i = y * recipe.getRecipeWidth() + x;
                if (i < recipe.getIngredients().size()) {
                    ingredientCycler.getCycledIngredientStack(recipe.getIngredients().get(i), i).ifPresent(itemStack -> {
                        GuiHelper.drawItemStack(guiGraphics, itemStack, stackX, stackY);
                        if (GuiHelper.isMouseBetween(mouseX, mouseY, stackX, stackY, 15, 15)) {
                            tooltips = GuiHelper.getTooltip(itemStack);
                        }
                    });
                }
            }
        }
    }
}

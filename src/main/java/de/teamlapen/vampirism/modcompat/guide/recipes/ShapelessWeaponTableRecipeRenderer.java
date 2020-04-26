package de.teamlapen.vampirism.modcompat.guide.recipes;

import amerifrance.guideapi.api.impl.Book;
import amerifrance.guideapi.api.impl.abstraction.CategoryAbstract;
import amerifrance.guideapi.api.impl.abstraction.EntryAbstract;
import amerifrance.guideapi.api.util.GuiHelper;
import amerifrance.guideapi.api.util.IngredientCycler;
import amerifrance.guideapi.gui.BaseScreen;
import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.inventory.recipes.ShapelessWeaponTableRecipe;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;


public class ShapelessWeaponTableRecipeRenderer extends BasicWeaponTableRecipeRenderer<ShapelessWeaponTableRecipe> {
    public ShapelessWeaponTableRecipeRenderer(ShapelessWeaponTableRecipe recipe) {
        super(recipe);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void draw(Book book, CategoryAbstract categoryAbstract, EntryAbstract entryAbstract, int guiLeft, int guiTop, int mouseX, int mouseY, BaseScreen baseScreen, FontRenderer fontRenderer, IngredientCycler ingredientCycler) {
        super.draw(book, categoryAbstract, entryAbstract, guiLeft, guiTop, mouseX, mouseY, baseScreen, fontRenderer, ingredientCycler);
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                int i = 3 * y + x;
                if (i < recipe.getIngredients().size()) {
                    int stackX = (x + 1) * 17 + (guiLeft + 49);
                    int stackY = (y + 1) * 17 + (guiTop + 30);
                    Ingredient ingredient = recipe.getIngredients().get(i);
                    ingredientCycler.getCycledIngredientStack(ingredient, i).ifPresent(stack -> {
                        GuiHelper.drawItemStack(stack, stackX, stackY);
                        if (GuiHelper.isMouseBetween(mouseX, mouseY, stackX, stackY, 15, 15)) {
                            tooltips = GuiHelper.getTooltip(stack);
                        }
                    });
                }
            }
        }
    }

    @Override
    protected String getRecipeName() {
        return UtilLib.translate("guideapi.text.crafting.shapeless");
    }
}

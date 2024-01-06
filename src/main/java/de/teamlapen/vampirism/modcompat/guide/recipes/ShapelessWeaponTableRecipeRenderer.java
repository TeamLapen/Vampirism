package de.teamlapen.vampirism.modcompat.guide.recipes;

import de.maxanier.guideapi.api.impl.Book;
import de.maxanier.guideapi.api.impl.abstraction.CategoryAbstract;
import de.maxanier.guideapi.api.impl.abstraction.EntryAbstract;
import de.maxanier.guideapi.api.util.GuiHelper;
import de.maxanier.guideapi.api.util.IngredientCycler;
import de.maxanier.guideapi.gui.BaseScreen;
import de.teamlapen.vampirism.recipes.ShapelessWeaponTableRecipe;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.crafting.Ingredient;


import org.jetbrains.annotations.NotNull;


public class ShapelessWeaponTableRecipeRenderer extends BasicWeaponTableRecipeRenderer<ShapelessWeaponTableRecipe> {
    public ShapelessWeaponTableRecipeRenderer(ShapelessWeaponTableRecipe recipe) {
        super(recipe);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void draw(@NotNull GuiGraphics guiGraphics, RegistryAccess registryAccess, Book book, CategoryAbstract categoryAbstract, EntryAbstract entryAbstract, int guiLeft, int guiTop, int mouseX, int mouseY, @NotNull BaseScreen baseScreen, @NotNull Font fontRenderer, @NotNull IngredientCycler ingredientCycler) {
        super.draw(guiGraphics, registryAccess, book, categoryAbstract, entryAbstract, guiLeft, guiTop, mouseX, mouseY, baseScreen, fontRenderer, ingredientCycler);
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                int i = 3 * y + x;
                if (i < recipe.getIngredients().size()) {
                    int stackX = (x + 1) * 17 + (guiLeft + 49);
                    int stackY = (y + 1) * 17 + (guiTop + 30);
                    Ingredient ingredient = recipe.getIngredients().get(i);
                    ingredientCycler.getCycledIngredientStack(ingredient, i).ifPresent(itemStack -> {
                        GuiHelper.drawItemStack(guiGraphics, itemStack, stackX, stackY);
                        if (GuiHelper.isMouseBetween(mouseX, mouseY, stackX, stackY, 15, 15)) {
                            tooltips = GuiHelper.getTooltip(itemStack);
                        }
                    });
                }
            }
        }
    }

    @Override
    protected @NotNull MutableComponent getRecipeName() {
        return Component.translatable("guideapi.text.crafting.shapeless");
    }
}

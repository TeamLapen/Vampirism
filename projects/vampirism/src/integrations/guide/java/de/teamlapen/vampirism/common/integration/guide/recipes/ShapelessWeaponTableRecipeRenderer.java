package de.teamlapen.vampirism.common.integration.guide.recipes;


import de.maxanier.guideapi.api.GuideBookScreen;
import de.maxanier.guideapi.api.book.Book;
import de.maxanier.guideapi.api.category.CategoryBase;
import de.maxanier.guideapi.api.entry.EntryBase;
import de.maxanier.guideapi.api.util.GuiHelper;
import de.maxanier.guideapi.api.util.IngredientCycler;
import de.teamlapen.vampirism.common.world.items.recipes.ShapelessWeaponTableRecipe;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.display.ShapelessCraftingRecipeDisplay;
import org.jetbrains.annotations.NotNull;

import java.util.List;


public class ShapelessWeaponTableRecipeRenderer extends BasicWeaponTableRecipeRenderer<ShapelessWeaponTableRecipe, ShapelessCraftingRecipeDisplay> {

    protected List<List<ItemStack>> inputs;

    public ShapelessWeaponTableRecipeRenderer(RecipeHolder<ShapelessWeaponTableRecipe> recipe) {
        super(recipe, ShapelessCraftingRecipeDisplay.class);
    }

    @Override
    public void draw(@NotNull GuiGraphics guiGraphics, Book book, CategoryBase categoryAbstract, EntryBase entryAbstract, int pageLeft, int pageTop, int mouseX, int mouseY, @NotNull GuideBookScreen baseScreen, @NotNull Font fontRenderer, @NotNull IngredientCycler ingredientCycler) {
        super.draw(guiGraphics, book, categoryAbstract, entryAbstract, pageLeft, pageTop, mouseX, mouseY, baseScreen, fontRenderer, ingredientCycler);

        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                int i = 3 * y + x;
                if (i < inputs.size()) {
                    int stackX = (x + 1) * 17 + (pageLeft - 39 + 49);
                    int stackY = (y + 1) * 17 + (pageTop - 13 + 30);
                    ItemStack itemStack = ingredientCycler.getCycledIngredientStack(inputs.get(i), i);
                    GuiHelper.drawItemStack(guiGraphics, itemStack, stackX, stackY);
                    if (GuiHelper.isMouseBetween(mouseX, mouseY, stackX, stackY, 15, 15)) {
                        tooltips = GuiHelper.getTooltip(itemStack);
                    }
                }
            }
        }
    }

    @Override
    protected @NotNull MutableComponent getRecipeName() {
        return Component.translatable("guideapi.text.crafting.shapeless");
    }

    @Override
    public void init(ContextMap context) {
        super.init(context);
        inputs = display().map(d -> d.ingredients().stream().map(sd -> sd.resolveForStacks(context)).toList()).orElse(List.of());
    }
}

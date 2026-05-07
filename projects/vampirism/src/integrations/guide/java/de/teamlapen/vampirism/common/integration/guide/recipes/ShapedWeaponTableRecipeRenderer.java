package de.teamlapen.vampirism.common.integration.guide.recipes;

import de.maxanier.guideapi.api.GuideBookScreen;
import de.maxanier.guideapi.api.book.Book;
import de.maxanier.guideapi.api.category.CategoryBase;
import de.maxanier.guideapi.api.entry.EntryBase;
import de.maxanier.guideapi.api.util.GuiHelper;
import de.maxanier.guideapi.api.util.IngredientCycler;
import de.teamlapen.vampirism.common.world.items.recipes.ShapedWeaponTableRecipe;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.display.ShapedCraftingRecipeDisplay;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ShapedWeaponTableRecipeRenderer extends BasicWeaponTableRecipeRenderer<ShapedWeaponTableRecipe, ShapedCraftingRecipeDisplay> {

    protected List<List<ItemStack>> inputs;

    public ShapedWeaponTableRecipeRenderer(RecipeHolder<ShapedWeaponTableRecipe> recipe) {
        super(recipe, ShapedCraftingRecipeDisplay.class);
    }

    @Override
    public void draw(@NotNull GuiGraphicsExtractor guiGraphics, Book book, CategoryBase categoryAbstract, EntryBase entryAbstract, int pageLeft, int pageTop, int mouseX, int mouseY, @NotNull GuideBookScreen baseScreen, @NotNull Font fontRenderer, @NotNull IngredientCycler ingredientCycler) {
        super.draw(guiGraphics, book, categoryAbstract, entryAbstract, pageLeft, pageTop, mouseX, mouseY, baseScreen, fontRenderer, ingredientCycler);

        display().ifPresent(d-> {
            for (int y = 0; y < d.height(); y++) {
                for (int x = 0; x < d.width(); x++) {
                    int stackX = (x + 1) * 17 + (pageLeft - 39 + 49);
                    int stackY = (y + 1) * 17 + (pageTop - 13 + 30);
                    int i = y * d.width() + x;
                    if (i < inputs.size()) {
                        ItemStack itemStack = ingredientCycler.getCycledIngredientStack(inputs.get(i), i);
                        GuiHelper.drawItemStack(guiGraphics, itemStack, stackX, stackY);
                        if (GuiHelper.isMouseBetween(mouseX, mouseY, stackX, stackY, 15, 15)) {
                            tooltips = GuiHelper.getTooltip(itemStack);
                        }
                    }
                }
            }
        });

    }

    @Override
    public void init(ContextMap context) {
        super.init(context);
        inputs = display().map(d -> d.ingredients().stream().map(sd -> sd.resolveForStacks(context)).toList()).orElse(List.of());

    }
}

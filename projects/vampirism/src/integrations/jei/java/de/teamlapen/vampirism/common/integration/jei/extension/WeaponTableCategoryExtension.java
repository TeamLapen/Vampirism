package de.teamlapen.vampirism.common.integration.jei.extension;

import de.teamlapen.vampirism.common.world.items.recipes.IWeaponTableRecipe;
import de.teamlapen.vampirism.common.world.items.recipes.ShapedWeaponTableRecipe;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.ICraftingGridHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.extensions.IRecipeCategoryExtension;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.ShapedCraftingRecipeDisplay;
import net.minecraft.world.item.crafting.display.ShapelessCraftingRecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class WeaponTableCategoryExtension implements IRecipeCategoryExtension<IWeaponTableRecipe> {


    public int getWidth(RecipeHolder<IWeaponTableRecipe> recipeHolder) {
        IWeaponTableRecipe value = recipeHolder.value();

        if (value instanceof ShapedWeaponTableRecipe shaped) {
            return shaped.getWidth();
        }

        return 0;
    }

    public int getHeight(RecipeHolder<IWeaponTableRecipe> recipeHolder) {
        IWeaponTableRecipe value = recipeHolder.value();

        if (value instanceof ShapedWeaponTableRecipe shaped) {
            return shaped.getHeight();
        }

        return 0;
    }

    public boolean isHandled(RecipeHolder<IWeaponTableRecipe> recipe) {
        IWeaponTableRecipe value = recipe.value();

        List<RecipeDisplay> display = value.display();
        if (display.isEmpty()) {
            return false;
        }

        RecipeDisplay first = display.getFirst();
        return first instanceof ShapelessCraftingRecipeDisplay || first instanceof ShapedCraftingRecipeDisplay;

    }

    public @NotNull List<SlotDisplay> getIngredients(RecipeHolder<IWeaponTableRecipe> recipeHolder) {
        List<RecipeDisplay> displays = recipeHolder.value().display();
        if (displays.isEmpty()) {
            return List.of();
        }
        RecipeDisplay display = displays.getFirst();
        if (display instanceof ShapedCraftingRecipeDisplay shapedCraftingRecipeDisplay) {
            return shapedCraftingRecipeDisplay.ingredients();
        } else if (display instanceof ShapelessCraftingRecipeDisplay shapelessCraftingRecipeDisplay) {
            return shapelessCraftingRecipeDisplay.ingredients();
        } else {
            return List.of();
        }
    }

    public void setRecipe(RecipeHolder<IWeaponTableRecipe> recipeHolder, IRecipeLayoutBuilder builder, ICraftingGridHelper craftingGridHelper, IFocusGroup focuses) {
        IWeaponTableRecipe recipe = recipeHolder.value();
        RecipeDisplay display = recipe.display().getFirst();
        SlotDisplay resultItem = display.result();
        craftingGridHelper.createAndSetOutputs(builder, resultItem);

        List<SlotDisplay> ingredients = getIngredients(recipeHolder);
        int width = getWidth(recipeHolder);
        int height = getHeight(recipeHolder);
        craftingGridHelper.createAndSetIngredientsFromDisplays(builder, ingredients, width, height);
    }
}

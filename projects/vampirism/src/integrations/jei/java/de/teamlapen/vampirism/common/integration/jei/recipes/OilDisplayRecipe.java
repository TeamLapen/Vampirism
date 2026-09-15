package de.teamlapen.vampirism.common.integration.jei.recipes;

import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.ShapelessCraftingRecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;

import java.util.List;

public class OilDisplayRecipe extends ShapelessRecipe {

    private final List<SlotDisplay> ingredientDisplays;
    private final SlotDisplay resultDisplay;

    public OilDisplayRecipe(ItemStackTemplate result, List<Ingredient> ingredients, List<SlotDisplay> ingredientDisplays, SlotDisplay resultDisplay) {
        super(new Recipe.CommonInfo(false), new CraftingRecipe.CraftingBookInfo(CraftingBookCategory.EQUIPMENT, ""), result, ingredients);
        this.ingredientDisplays = ingredientDisplays;
        this.resultDisplay = resultDisplay;
    }

    public static SlotDisplay cycle(List<ItemStackTemplate> stacks) {
        return new SlotDisplay.Composite(stacks.stream().map(s -> (SlotDisplay) new SlotDisplay.ItemStackSlotDisplay(s)).toList());
    }

    @Override
    public List<RecipeDisplay> display() {
        return List.of(new ShapelessCraftingRecipeDisplay(this.ingredientDisplays, this.resultDisplay, new SlotDisplay.ItemSlotDisplay(Items.CRAFTING_TABLE)));
    }
}

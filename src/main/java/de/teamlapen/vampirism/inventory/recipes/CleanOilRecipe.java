package de.teamlapen.vampirism.inventory.recipes;

import de.teamlapen.vampirism.core.ModRecipes;
import de.teamlapen.vampirism.util.OilUtils;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class CleanOilRecipe extends SpecialRecipe {

    public CleanOilRecipe(ResourceLocation id) {
        super(id);
    }

    @Override
    public boolean matches(CraftingInventory inventory, World level) {
        ItemStack tool = null;
        ItemStack paper = null;
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (!stack.isEmpty()) {
                if (stack.getItem() == Items.PAPER) {
                    if (paper == null) {
                        paper = stack;
                    }
                } else if(OilUtils.hasAppliedOil(stack)) {
                    if (tool != null) return false;
                    tool = stack;
                } else {
                    return false;
                }
            }
        }
        return tool != null && paper != null;
    }

    @Override
    public ItemStack assemble(CraftingInventory inventory) {
        ItemStack tool = ItemStack.EMPTY;
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (!stack.isEmpty() && OilUtils.hasAppliedOil(stack)) {
                tool = stack;
                break;
            }
        }
        ItemStack result = tool.copy();
        OilUtils.removeAppliedOil(result);
        return result;
    }

    @Override
    public boolean canCraftInDimensions(int x, int y) {
        return x * y >= 2;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return ModRecipes.CLEAN_OIL.get();
    }
}

package de.teamlapen.vampirism.inventory.recipes;

import de.teamlapen.vampirism.api.items.IOilItem;
import de.teamlapen.vampirism.api.items.oil.IApplicableOil;
import de.teamlapen.vampirism.api.items.oil.IOil;
import de.teamlapen.vampirism.core.ModRecipes;
import de.teamlapen.vampirism.util.OilUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.NotNull;

public class ApplicableOilRecipe extends CustomRecipe {

    public ApplicableOilRecipe(ResourceLocation id) {
        super(id);
    }

    @Override
    public boolean matches(CraftingContainer inventory, @NotNull Level world) {
        IApplicableOil oil = null;
        ItemStack tool = null;
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (!stack.isEmpty()) {
                if (stack.getItem() instanceof IOilItem) {
                    if (oil != null) return false;
                    IOil oil1 = ((IOilItem) stack.getItem()).getOil(stack);
                    if (oil1 instanceof IApplicableOil) {
                        oil = ((IApplicableOil) oil1);
                    }
                } else {
                    if (tool != null) return false;
                    tool = stack;
                }
            }
        }
        return oil != null && tool != null && oil.canBeApplied(tool);
    }

    @NotNull
    @Override
    public ItemStack assemble(@NotNull CraftingContainer inventory) {
        ItemStack oilStack = ItemStack.EMPTY;
        ItemStack toolStack = ItemStack.EMPTY;
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (!stack.isEmpty()) {
                if (stack.getItem() instanceof IOilItem) {
                    oilStack = stack;
                } else {
                    toolStack = stack;
                }
            }
        }
        ItemStack result = toolStack.copy();
        if (oilStack.isEmpty() || toolStack.isEmpty()) return result;
        IOil oil = ((IOilItem) oilStack.getItem()).getOil(oilStack);
        if (oil instanceof IApplicableOil) {
            OilUtils.setAppliedOil(result, ((IApplicableOil) oil));
        }
        return result;
    }

    @Override
    public boolean canCraftInDimensions(int x, int y) {
        return x*y >= 2;
    }

    @NotNull
    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.APPLICABLE_OIL.get();
    }
}

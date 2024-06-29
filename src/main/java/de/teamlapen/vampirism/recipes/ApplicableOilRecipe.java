package de.teamlapen.vampirism.recipes;

import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.items.IFactionExclusiveItem;
import de.teamlapen.vampirism.api.items.IOilItem;
import de.teamlapen.vampirism.api.items.oil.IApplicableOil;
import de.teamlapen.vampirism.api.items.oil.IOil;
import de.teamlapen.vampirism.core.ModFactions;
import de.teamlapen.vampirism.core.ModRecipes;
import de.teamlapen.vampirism.items.component.AppliedOilContent;
import de.teamlapen.vampirism.items.component.OilContent;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class ApplicableOilRecipe extends CustomRecipe {

    public ApplicableOilRecipe(@NotNull CraftingBookCategory category) {
        super(category);
    }

    @Override
    public boolean matches(@NotNull CraftingInput inventory, @NotNull Level world) {
        IApplicableOil oil = null;
        ItemStack tool = null;
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (!stack.isEmpty()) {
                if (stack.getItem() instanceof IOilItem) {
                    if (oil != null) return false;
                    Holder<IOil> oil1 = ((IOilItem) stack.getItem()).getOil(stack);
                    if (oil1.value() instanceof IApplicableOil applicableOil) {
                        oil = applicableOil;
                    }
                } else {
                    if (tool != null) return false;
                    tool = stack;
                }
            }
        }
        return oil != null && tool != null && (!(tool.getItem() instanceof IFactionExclusiveItem) || IFaction.is(ModFactions.HUNTER, ((IFactionExclusiveItem) tool.getItem()).getExclusiveFaction(tool))) && oil.canBeApplied(tool);
    }

    @NotNull
    @Override
    public ItemStack assemble(@NotNull CraftingInput inventory, @NotNull HolderLookup.Provider registryAccess) {
        ItemStack oilStack = ItemStack.EMPTY;
        ItemStack toolStack = ItemStack.EMPTY;
        for (int i = 0; i < inventory.size(); i++) {
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
        Holder<IOil> oil = OilContent.getOil(oilStack);
        if (oil.value() instanceof IApplicableOil) {
            AppliedOilContent.apply(result, ((Holder<IApplicableOil>) (Object) oil));
        }
        return result;
    }

    @Override
    public boolean canCraftInDimensions(int x, int y) {
        return x * y >= 2;
    }

    @NotNull
    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.APPLICABLE_OIL.get();
    }
}

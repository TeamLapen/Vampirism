package de.teamlapen.vampirism.common.world.items.recipes;

import de.teamlapen.vampirism.common.core.ModItems;
import de.teamlapen.vampirism.common.core.ModRecipes;
import de.teamlapen.vampirism.common.world.items.BloodBottleItem;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

// TODO: Add this to JEI when it's available
public class FillBottleFromSyringeRecipe extends CustomRecipe {

    public FillBottleFromSyringeRecipe(CraftingBookCategory category) {
        super(category);
    }

    @Override
    public boolean matches(@NotNull CraftingInput input, @NotNull Level level) {
        ItemStack bottle = ItemStack.EMPTY;
        int syringes = 0;

        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (stack.isEmpty()) continue;

            if (stack.getItem() instanceof BloodBottleItem) {
                if (!bottle.isEmpty()) return false;
                bottle = stack;
            } else if (stack.is(ModItems.SYRINGE_BLOOD.get())) {
                syringes++;
            } else if (stack.is(Items.GLASS_BOTTLE)) {
                if (!bottle.isEmpty()) return false;
                bottle = stack;
            } else {
                return false;
            }
        }

        if (syringes == 0 || bottle.isEmpty()) return false;

        int blood = (bottle.getItem() instanceof BloodBottleItem) ? BloodBottleItem.getBlood(bottle) : 0;

        return blood + syringes <= BloodBottleItem.AMOUNT;
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull CraftingInput input, HolderLookup.@NotNull Provider registries) {
        ItemStack bottle = ItemStack.EMPTY;
        int syringes = 0;

        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (stack.isEmpty()) continue;

            if (stack.getItem() instanceof BloodBottleItem || stack.is(Items.GLASS_BOTTLE)) {
                bottle = stack.copyWithCount(1);
            } else if (stack.is(ModItems.SYRINGE_BLOOD.get())) {
                syringes++;
            }
        }

        int blood = (bottle.getItem() instanceof BloodBottleItem) ? BloodBottleItem.getBlood(bottle) : 0;

        return BloodBottleItem.createStackWithBlood(blood + syringes);
    }

    @Override
    public @NotNull RecipeSerializer<? extends CustomRecipe> getSerializer() {
        return ModRecipes.FILL_BOTTLE_FROM_SYRINGE.get();
    }
}

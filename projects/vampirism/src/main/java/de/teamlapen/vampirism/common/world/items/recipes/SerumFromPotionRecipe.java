package de.teamlapen.vampirism.common.world.items.recipes;

import de.teamlapen.vampirism.common.core.ModItems;
import de.teamlapen.vampirism.common.core.ModRecipes;
import de.teamlapen.vampirism.common.world.items.SerumInjectionItem;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import java.util.*;

public class SerumFromPotionRecipe extends CustomRecipe {

    public SerumFromPotionRecipe(CraftingBookCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        boolean hasPotion = false;
        boolean hasSyringe = false;

        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (stack.isEmpty()) continue;

            if (stack.is(Items.POTION)) {
                PotionContents contents = stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
                if (contents.potion().isPresent() && SerumInjectionItem.isBlockedPotion(contents.potion().get())) {
                    return false;
                }
                hasPotion = true;
            } else if (stack.is(ModItems.SYRINGE_EMPTY.get())) {
                hasSyringe = true;
            } else {
                return false;
            }
        }

        return hasPotion && hasSyringe;
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        PotionContents contents = null;

        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (stack.is(Items.POTION)) {
                contents = stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
                break;
            }
        }

        if (contents == null) return ItemStack.EMPTY;

        ItemStack result = ModItems.SERUM_INJECTION.get().getDefaultInstance();
        result.set(DataComponents.POTION_CONTENTS, contents);

        return result;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingInput input) {
        NonNullList<ItemStack> remaining = NonNullList.withSize(input.size(), ItemStack.EMPTY);

        for (int i = 0; i < input.size(); i++) {
            if (input.getItem(i).is(Items.POTION)) {
                remaining.set(i, new ItemStack(Items.GLASS_BOTTLE));
            }
        }

        return remaining;
    }

    @Override
    public RecipeSerializer<SerumFromPotionRecipe> getSerializer() {
        return ModRecipes.SERUM_FROM_POTION.get();
    }
}

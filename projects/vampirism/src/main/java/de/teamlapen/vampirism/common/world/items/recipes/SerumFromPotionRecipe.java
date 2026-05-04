package de.teamlapen.vampirism.common.world.items.recipes;

import com.mojang.serialization.MapCodec;
import de.teamlapen.vampirism.common.core.ModItems;
import de.teamlapen.vampirism.common.core.ModRecipes;
import de.teamlapen.vampirism.common.world.items.SerumInjectionItem;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

public class SerumFromPotionRecipe extends CustomRecipe {

    public static final SerumFromPotionRecipe INSTANCE = new SerumFromPotionRecipe();
    public static final MapCodec<SerumFromPotionRecipe> CODEC = MapCodec.unit(INSTANCE);
    public static final StreamCodec<RegistryFriendlyByteBuf, SerumFromPotionRecipe> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    public SerumFromPotionRecipe() {
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        boolean hasPotion = false;
        int syringeCount = 0;

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
                syringeCount++;
            } else {
                return false;
            }
        }

        return hasPotion && syringeCount == 4;
    }

    @Override
    public ItemStack assemble(CraftingInput input) {
        PotionContents contents = null;

        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (stack.is(Items.POTION)) {
                contents = stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
                break;
            }
        }

        if (contents == null) return ItemStack.EMPTY;

        ItemStack result = new ItemStack(ModItems.SERUM_INJECTION.get(), 4);
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

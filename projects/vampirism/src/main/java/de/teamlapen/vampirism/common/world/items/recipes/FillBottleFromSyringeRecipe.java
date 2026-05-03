package de.teamlapen.vampirism.common.world.items.recipes;

import com.mojang.serialization.MapCodec;
import de.teamlapen.vampirism.common.core.ModItems;
import de.teamlapen.vampirism.common.core.ModRecipes;
import de.teamlapen.vampirism.common.world.items.BloodBottleItem;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class FillBottleFromSyringeRecipe extends CustomRecipe {

    public static final FillBottleFromSyringeRecipe INSTANCE = new FillBottleFromSyringeRecipe();
    public static final MapCodec<FillBottleFromSyringeRecipe> CODEC = MapCodec.unit(INSTANCE);
    public static final StreamCodec<RegistryFriendlyByteBuf, FillBottleFromSyringeRecipe> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    @Override
    public boolean matches(CraftingInput input, Level level) {
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
    public ItemStack assemble(CraftingInput input) {
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
    public RecipeSerializer<? extends CustomRecipe> getSerializer() {
        return ModRecipes.FILL_BOTTLE_FROM_SYRINGE.get();
    }
}

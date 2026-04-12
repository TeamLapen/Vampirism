package de.teamlapen.vampirism.common.world.items.recipes;

import com.mojang.serialization.MapCodec;
import de.teamlapen.vampirism.common.core.ModRecipes;
import de.teamlapen.vampirism.common.world.items.component.AppliedOilContent;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class CleanOilRecipe extends CustomRecipe {

    public static final CleanOilRecipe INSTANCE = new CleanOilRecipe();
    public static final MapCodec<CleanOilRecipe> CODEC = MapCodec.unit(INSTANCE);
    public static final StreamCodec<RegistryFriendlyByteBuf, CleanOilRecipe> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    @Override
    public boolean matches(CraftingInput inventory, @NotNull Level level) {
        ItemStack tool = null;
        ItemStack paper = null;
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (!stack.isEmpty()) {
                if (stack.getItem() == Items.PAPER) {
                    if (paper == null) {
                        paper = stack;
                    }
                } else if (AppliedOilContent.getAppliedOil(stack).isPresent()) {
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
    public @NotNull ItemStack assemble(CraftingInput inventory) {
        ItemStack tool = ItemStack.EMPTY;
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (!stack.isEmpty() && AppliedOilContent.getAppliedOil(stack).isPresent()) {
                tool = stack;
                break;
            }
        }
        ItemStack result = tool.copy();
        AppliedOilContent.remove(result);
        return result;
    }

    @Override
    public @NotNull RecipeSerializer<CleanOilRecipe> getSerializer() {
        return ModRecipes.CLEAN_OIL.get();
    }
}

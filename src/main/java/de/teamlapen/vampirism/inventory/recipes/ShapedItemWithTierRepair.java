package de.teamlapen.vampirism.inventory.recipes;

import com.google.gson.JsonObject;
import de.teamlapen.vampirism.api.items.IItemWithTier;
import de.teamlapen.vampirism.core.ModRecipes;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

/**
 * This recipe copies the {@link CompoundNBT} from the first found {@link IItemWithTier} and inserts it into the manufacturing result with damage = 0
 *
 * @author Cheaterpaul
 */
public class ShapedItemWithTierRepair extends ShapedRecipe {

    public ShapedItemWithTierRepair(ShapedRecipe shaped) {
        super(shaped.getId(), shaped.getGroup(), shaped.getRecipeWidth(), shaped.getRecipeHeight(), shaped.getIngredients(), shaped.getRecipeOutput());
    }

    @Override
    public ItemStack getCraftingResult(CraftingInventory inv) {
        ItemStack stack = null;
        search:
        for (int i = 0; i <= inv.getWidth(); ++i) {
            for (int j = 0; j <= inv.getHeight(); ++j) {
                if (inv.getStackInSlot(i + j * inv.getWidth()).getItem() instanceof IItemWithTier) {
                    stack = inv.getStackInSlot(i + j * inv.getWidth());
                    break search;
                }
            }
        }
        ItemStack result = super.getCraftingResult(inv);
        if (stack != null) {
            result.setTag(stack.getTag());
            result.setDamage(0);
        }
        return result;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return ModRecipes.repair_iitemwithtier;
    }

    public static class Serializer extends ShapedRecipe.Serializer {
        @Override
        public ShapedRecipe read(ResourceLocation recipeId, JsonObject json) {
            return new ShapedItemWithTierRepair(super.read(recipeId, json));
        }

        @Override
        public ShapedRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
            return new ShapedItemWithTierRepair(super.read(recipeId, buffer));
        }

        @Override
        public void write(PacketBuffer buffer, ShapedRecipe recipe) {
            super.write(buffer, recipe);
        }
    }
}
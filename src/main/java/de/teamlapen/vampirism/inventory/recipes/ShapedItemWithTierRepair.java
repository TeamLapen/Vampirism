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
        super(shaped.getId(), shaped.getGroup(), shaped.getRecipeWidth(), shaped.getRecipeHeight(), shaped.getIngredients(), shaped.getResultItem());
    }

    @Override
    public ItemStack assemble(CraftingInventory inv) {
        ItemStack stack = null;
        search:
        for (int i = 0; i <= inv.getWidth(); ++i) {
            for (int j = 0; j <= inv.getHeight(); ++j) {
                if (inv.getItem(i + j * inv.getWidth()).getItem() instanceof IItemWithTier) {
                    stack = inv.getItem(i + j * inv.getWidth());
                    break search;
                }
            }
        }
        ItemStack result = super.assemble(inv);
        if (stack != null) {
            result.setTag(stack.getTag());
            result.setDamageValue(0);
        }
        return result;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return ModRecipes.REPAIR_IITEMWITHTIER.get();
    }

    public static class Serializer extends ShapedRecipe.Serializer {
        @Override
        public ShapedRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
            return new ShapedItemWithTierRepair(super.fromJson(recipeId, json));
        }

        @Override
        public ShapedRecipe fromNetwork(ResourceLocation recipeId, PacketBuffer buffer) {
            return new ShapedItemWithTierRepair(super.fromNetwork(recipeId, buffer));
        }

        @Override
        public void toNetwork(PacketBuffer buffer, ShapedRecipe recipe) {
            super.toNetwork(buffer, recipe);
        }
    }
}
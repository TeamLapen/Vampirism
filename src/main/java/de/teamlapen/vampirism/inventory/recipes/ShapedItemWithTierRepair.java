package de.teamlapen.vampirism.inventory.recipes;

import com.google.gson.JsonObject;
import de.teamlapen.vampirism.api.items.IItemWithTier;
import de.teamlapen.vampirism.core.ModRecipes;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

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
    public ItemStack assemble(CraftingContainer inv) {
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
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.repair_iitemwithtier;
    }

    public static class Serializer extends ShapedRecipe.Serializer {
        @Override
        public ShapedRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
            return new ShapedItemWithTierRepair(super.fromJson(recipeId, json));
        }

        @Override
        public ShapedRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            return new ShapedItemWithTierRepair(super.fromNetwork(recipeId, buffer));
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, ShapedRecipe recipe) {
            super.toNetwork(buffer, recipe);
        }
    }
}
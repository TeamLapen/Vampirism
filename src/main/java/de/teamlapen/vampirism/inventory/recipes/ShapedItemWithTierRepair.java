package de.teamlapen.vampirism.inventory.recipes;

import com.google.gson.JsonObject;
import de.teamlapen.vampirism.api.items.IItemWithTier;
import de.teamlapen.vampirism.core.ModRecipes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;

import org.jetbrains.annotations.NotNull;

/**
 * This recipe copies the {@link net.minecraft.nbt.CompoundTag} from the first found {@link IItemWithTier} and inserts it into the manufacturing result with damage = 0
 *
 * @author Cheaterpaul
 */
public class ShapedItemWithTierRepair extends ShapedRecipe {

    public ShapedItemWithTierRepair(ShapedRecipe shaped) {
        super(shaped.getId(), shaped.getGroup(), shaped.getRecipeWidth(), shaped.getRecipeHeight(), shaped.getIngredients(), shaped.getResultItem());
    }

    @NotNull
    @Override
    public ItemStack assemble(@NotNull CraftingContainer inv) {
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

    @NotNull
    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.REPAIR_IITEMWITHTIER.get();
    }

    public static class Serializer extends ShapedRecipe.Serializer {
        @NotNull
        @Override
        public ShapedRecipe fromJson(@NotNull ResourceLocation recipeId, @NotNull JsonObject json) {
            return new ShapedItemWithTierRepair(super.fromJson(recipeId, json));
        }

        @Override
        public ShapedRecipe fromNetwork(@NotNull ResourceLocation recipeId, @NotNull FriendlyByteBuf buffer) {
            return new ShapedItemWithTierRepair(super.fromNetwork(recipeId, buffer));
        }

        @Override
        public void toNetwork(@NotNull FriendlyByteBuf buffer, @NotNull ShapedRecipe recipe) {
            super.toNetwork(buffer, recipe);
        }
    }
}
package de.teamlapen.vampirism.recipes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import de.teamlapen.vampirism.api.items.IItemWithTier;
import de.teamlapen.vampirism.core.ModRecipes;
import de.teamlapen.vampirism.mixin.accessor.ShapedRecipeAccessor;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * This recipe copies the {@link net.minecraft.nbt.CompoundTag} from the first found {@link IItemWithTier} and inserts it into the manufacturing result with damage = 0
 *
 * @author Cheaterpaul
 */
public class ShapedItemWithTierRepair extends ShapedRecipe {

    public ShapedItemWithTierRepair(@NotNull ShapedRecipe shaped) {
        super(shaped.getGroup(), CraftingBookCategory.EQUIPMENT, ((ShapedRecipeAccessor) shaped).getPattern(), ((ShapedRecipeAccessor) shaped).getResult(), shaped.showNotification());
    }

    @NotNull
    @Override
    public ItemStack assemble(@NotNull CraftingContainer inv, @NotNull HolderLookup.Provider provider) {
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
        ItemStack result = super.assemble(inv, provider);
        if (stack != null) {
            result.applyComponents(stack.getComponents());
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

        public static final MapCodec<ShapedRecipe> CODEC = ShapedRecipe.Serializer.CODEC.xmap(ShapedItemWithTierRepair::new, ShapedItemWithTierRepair::new);

        @Override
        public @NotNull MapCodec<ShapedRecipe> codec() {
            return CODEC;
        }

    }
}
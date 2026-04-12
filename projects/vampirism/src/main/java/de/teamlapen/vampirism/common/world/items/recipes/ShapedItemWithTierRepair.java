package de.teamlapen.vampirism.common.world.items.recipes;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.api.world.items.IItemWithTier;
import de.teamlapen.vampirism.common.core.ModRecipes;
import de.teamlapen.vampirism.misc.mixin.accessor.ShapedRecipeAccessor;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.*;
import org.jetbrains.annotations.NotNull;

import java.util.function.UnaryOperator;

/**
 * This recipe copies the {@link net.minecraft.nbt.CompoundTag} from the first found {@link IItemWithTier} and inserts it into the manufacturing result with damage = 0
 *
 * @author Cheaterpaul
 */
public class ShapedItemWithTierRepair extends ShapedRecipe {

    public ShapedItemWithTierRepair(@NotNull ShapedRecipe shaped) {
        super(shaped.getCommonInfo(), shaped.getCraftingBookInfo(), shaped.getPattern(), shaped.getResult());
    }

    @NotNull
    @Override
    public ItemStack assemble(@NotNull CraftingInput inv) {
        ItemStack stack = null;
        search:
        for (int i = 0; i <= inv.width(); ++i) {
            for (int j = 0; j <= inv.height(); ++j) {
                if (inv.getItem(i + j * inv.width()).getItem() instanceof IItemWithTier) {
                    stack = inv.getItem(i + j * inv.width());
                    break search;
                }
            }
        }
        ItemStack result = super.assemble(inv);
        if (stack != null) {
            result.applyComponents(stack.getComponents());
            result.setDamageValue(0);
        }
        return result;
    }

    @NotNull
    @Override
    public RecipeSerializer<ShapedRecipe> getSerializer() {
        return ModRecipes.REPAIR_IITEMWITHTIER.get();
    }

    public static final MapCodec<ShapedItemWithTierRepair> CODEC = ShapedRecipe.MAP_CODEC.xmap(ShapedItemWithTierRepair::new, x -> x);
    public static final StreamCodec<RegistryFriendlyByteBuf, ShapedItemWithTierRepair> STREAM_CODEC = ShapedRecipe.STREAM_CODEC.map(ShapedItemWithTierRepair::new, x -> x);
}
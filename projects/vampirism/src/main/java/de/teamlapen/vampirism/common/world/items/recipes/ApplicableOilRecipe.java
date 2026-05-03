package de.teamlapen.vampirism.common.world.items.recipes;

import com.mojang.serialization.MapCodec;
import de.teamlapen.faction.common.components.FactionRestriction;
import de.teamlapen.vampirism.api.world.items.IOilItem;
import de.teamlapen.vampirism.api.world.items.oil.IApplicableOil;
import de.teamlapen.vampirism.api.world.items.oil.IOil;
import de.teamlapen.vampirism.common.core.ModFactions;
import de.teamlapen.vampirism.common.core.ModRecipes;
import de.teamlapen.vampirism.common.world.items.component.AppliedOilContent;
import de.teamlapen.vampirism.common.world.items.component.OilContent;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class ApplicableOilRecipe extends CustomRecipe {
    public static final ApplicableOilRecipe INSTANCE = new ApplicableOilRecipe();
    public static final MapCodec<ApplicableOilRecipe> CODEC = MapCodec.unit(INSTANCE);
    public static final StreamCodec<RegistryFriendlyByteBuf, ApplicableOilRecipe> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    @Override
    public boolean matches(CraftingInput inventory, Level world) {
        IApplicableOil oil = null;
        ItemStack tool = null;
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (!stack.isEmpty()) {
                if (stack.getItem() instanceof IOilItem) {
                    if (oil != null) return false;
                    Holder<IOil> oil1 = ((IOilItem) stack.getItem()).getOil(stack);
                    if (oil1.value() instanceof IApplicableOil applicableOil) {
                        oil = applicableOil;
                    }
                } else {
                    if (tool != null) return false;
                    tool = stack;
                }
            }
        }
        return oil != null && tool != null && FactionRestriction.matchFaction(tool, ModFactions.HUNTER) && oil.canBeApplied(tool);
    }

    @Override
    public ItemStack assemble(CraftingInput inventory) {
        ItemStack oilStack = ItemStack.EMPTY;
        ItemStack toolStack = ItemStack.EMPTY;
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (!stack.isEmpty()) {
                if (stack.getItem() instanceof IOilItem) {
                    oilStack = stack;
                } else {
                    toolStack = stack;
                }
            }
        }
        ItemStack result = toolStack.copy();
        if (oilStack.isEmpty() || toolStack.isEmpty()) return result;
        Holder<IOil> oil = OilContent.getOil(oilStack);
        if (oil.value() instanceof IApplicableOil) {
            //noinspection unchecked
            AppliedOilContent.apply(result, ((Holder<IApplicableOil>) (Object) oil));
        }
        return result;
    }

    @Override
    public RecipeSerializer<ApplicableOilRecipe> getSerializer() {
        return ModRecipes.APPLICABLE_OIL.get();
    }
}

package de.teamlapen.vampirism.items;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.api.items.IItemWithTier;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Implements the methods as default methods using the itemstack NBT compound
 */
public interface IItemWithTierNBTImpl extends IItemWithTier {

    @SideOnly(Side.CLIENT)
    default void addTierInformation(ItemStack stack, List<String> tooltip) {
        TIER t = getTier(stack);
        if (t != TIER.NORMAL) {
            tooltip.add(TextFormatting.AQUA + UtilLib.translate("text.vampirism.item_tier." + t.name().toLowerCase()));
        }
    }

    @Override
    default TIER getTier(@Nonnull ItemStack stack) {
        return IItemWithTier.getTierStatic(stack);
    }

    @Nonnull
    @Override
    default ItemStack setTier(@Nonnull ItemStack stack, TIER tier) {
        return IItemWithTier.setTierStatic(stack, tier);
    }
}

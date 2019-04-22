package de.teamlapen.vampirism.items;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.items.IItemWithTier;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Implements the methods as default methods using the itemstack NBT compound
 */
public interface IItemWithTierNBTImpl extends IItemWithTier {

    static TIER getTierStatic(@Nonnull ItemStack stack) {
        NBTTagCompound tag = UtilLib.checkNBT(stack);
        if (tag.hasKey("tier")) {
            try {
                return TIER.valueOf(tag.getString("tier"));
            } catch (IllegalArgumentException e) {
                VampirismMod.log.e(stack.getItem().getRegistryName().toString(), e, "Unknown item tier %s", tag.getString("tier"));
            }

        }
        return TIER.NORMAL;
    }

    @Nonnull
    static ItemStack setTierStatic(@Nonnull ItemStack stack, TIER tier) {
        NBTTagCompound tag = UtilLib.checkNBT(stack);
        tag.setString("tier", tier.name());
        return stack;
    }

    @OnlyIn(Dist.CLIENT)
    default void addTierInformation(ItemStack stack, List<String> tooltip) {
        TIER t = getTier(stack);
        if (t != TIER.NORMAL) {
            tooltip.add(TextFormatting.AQUA + UtilLib.translate("text.vampirism.item_tier." + t.name().toLowerCase()));
        }
    }

    @Override
    default TIER getTier(@Nonnull ItemStack stack) {
        return IItemWithTierNBTImpl.getTierStatic(stack);
    }

    @Nonnull
    @Override
    default ItemStack setTier(@Nonnull ItemStack stack, TIER tier) {
        return IItemWithTierNBTImpl.setTierStatic(stack, tier);
    }
}

package de.teamlapen.vampirism.items;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.EnumStrength;
import de.teamlapen.vampirism.api.items.IItemWithTier;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * HolyWaterBottle
 * Exists in different tiers and as splash versions.
 */
public class ItemHolyWaterBottle extends VampirismItem implements IItemWithTier {

    public static final String regName = "holy_water_bottle";

    public ItemHolyWaterBottle(String regName) {
        super(regName);
        this.setMaxStackSize(1);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        TIER t = getTier(stack);
        if (t != TIER.NORMAL) {
            tooltip.add(TextFormatting.AQUA + UtilLib.translate("text.vampirism.item_tier." + t.name().toLowerCase()));
        }
    }


    /**
     * @param tier
     * @return A stack of this item with the given tier
     */
    public ItemStack getStack(TIER tier) {
        return setTier(new ItemStack(this), tier);
    }

    /**
     * Converts the tier of this bottle into the strength of the applied holy water
     *
     * @param tier
     * @return
     */
    public EnumStrength getStrength(TIER tier) {
        switch (tier) {
            case NORMAL:
                return EnumStrength.WEAK;
            case ENHANCED:
                return EnumStrength.MEDIUM;
            case ULTIMATE:
                return EnumStrength.STRONG;
        }
        return EnumStrength.NONE;
    }


    @Override
    public void getSubItems(ItemGroup tab, NonNullList<ItemStack> items) {
        if (isInCreativeTab(tab)) {
            for (TIER t : TIER.values()) {
                items.add(setTier(new ItemStack(this), t));
            }
        }
    }


    @Override
    public TIER getTier(@Nonnull ItemStack stack) {
        NBTTagCompound tag = UtilLib.checkNBT(stack);
        if (tag.hasKey("tier")) {
            try {
                return TIER.valueOf(tag.getString("tier"));
            } catch (IllegalArgumentException e) {
                VampirismMod.log.e("HolyWaterBottle", e, "Unknown item tier %s", tag.getString("tier"));
            }

        }
        return TIER.NORMAL;
    }


    @Nonnull
    @Override
    public ItemStack setTier(@Nonnull ItemStack stack, TIER tier) {
        NBTTagCompound tag = UtilLib.checkNBT(stack);
        tag.setString("tier", tier.name());
        return stack;
    }

}

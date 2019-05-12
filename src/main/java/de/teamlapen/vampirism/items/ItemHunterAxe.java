package de.teamlapen.vampirism.items;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.items.IItemWithTier;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Enchantments;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemHunterAxe extends VampirismHunterWeapon implements IItemWithTier {
    private static final String regName = "hunter_axe";


    public ItemHunterAxe() {
        super(regName, ToolMaterial.IRON, 0.37F);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        TIER tier = getTier(stack);
        if (tier != TIER.NORMAL) {
            tooltip.add(TextFormatting.AQUA + UtilLib.translate("text.vampirism.item_tier." + tier.name().toLowerCase()));

        }
        tooltip.add(UtilLib.translateFormatted("text.vampirism.deals_more_damage_to", Math.round((getVampireMult(tier) - 1) * 100)) + " " + UtilLib.translate(VReference.VAMPIRE_FACTION.getTranslationKeyPlural()));
    }


    @Override
    public float getDamageMultiplierForFaction(@Nonnull ItemStack stack) {
        return getVampireMult(getTier(stack));
    }

    @Override
    public int getMinLevel(@Nonnull ItemStack stack) {
        return getMinLevel(getTier(stack));
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
        NBTTagCompound nbt = UtilLib.checkNBT(stack);
        if (nbt.hasKey("tier")) {
            try {
                return TIER.valueOf(nbt.getString("tier"));
            } catch (IllegalArgumentException e) {
                VampirismMod.log.e("HunterAxe", e, "Cannot find tier %s", nbt.getString("tier"));
            }
        }
        return TIER.NORMAL;
    }

    @Override
    public boolean hasEffect(ItemStack stack) {
        return false;
    }

    @Nonnull
    @Override
    public ItemStack setTier(@Nonnull ItemStack stack, TIER tier) {
        NBTTagCompound nbt = UtilLib.checkNBT(stack);
        nbt.setString("tier", tier.name());
        Map<Enchantment, Integer> map = new HashMap<>();
        map.put(Enchantments.KNOCKBACK, getKnockback(tier));
        EnchantmentHelper.setEnchantments(map, stack);
        return stack;
    }

    private int getKnockback(TIER tier) {
        switch (tier) {
            case ULTIMATE:
                return 4;
            case ENHANCED:
                return 3;
            default:
                return 2;
        }
    }

    private int getMinLevel(TIER tier) {
        switch (tier) {
            case ULTIMATE:
                return 8;
            case ENHANCED:
                return 6;
            default:
                return 4;
        }
    }

    private float getVampireMult(TIER tier) {
        switch (tier) {
            case ULTIMATE:
                return 1.4F;
            case ENHANCED:
                return 1.3F;
            default:
                return 1.1F;
        }
    }


}

package de.teamlapen.vampirism.items;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.items.IItemWithTier;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemHunterAxe extends VampirismHunterWeapon implements IItemWithTier {
    private static final String regName = "hunterAxe";



    public ItemHunterAxe() {
        super(regName, ToolMaterial.IRON, -2.5F);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
        super.addInformation(stack, playerIn, tooltip, advanced);
        TIER tier = getTier(stack);
        if (tier != TIER.NORMAL) {
            tooltip.add(TextFormatting.AQUA + UtilLib.translateToLocal("text.vampirism.itemTier." + tier.name().toLowerCase()));

        }
        tooltip.add(UtilLib.translateToLocalFormatted("text.vampirism.deals_more_damage_to", Math.round((getVampireMult(tier) - 1) * 100)) + " " + UtilLib.translateToLocal(VReference.VAMPIRE_FACTION.getUnlocalizedNamePlural()));
    }

    @Override
    public float getDamageMultiplierForFaction(ItemStack stack) {
        return getVampireMult(getTier(stack));
    }

    @Override
    public int getMinLevel(ItemStack stack) {
        return getMinLevel(getTier(stack));
    }

    @Override
    public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {
        for (TIER t : TIER.values()) {
            subItems.add(setTier(new ItemStack(itemIn), t));
        }
    }

    @Override
    public TIER getTier(ItemStack stack) {
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

    @Override
    public ItemStack setTier(ItemStack stack, TIER tier) {
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

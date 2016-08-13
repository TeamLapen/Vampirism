package de.teamlapen.vampirism.items;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.VReference;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.text.TextFormatting;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemHunterAxe extends VampirismHunterWeapon {
    private static final String regName = "hunterAxe";

    public static TYPE getType(ItemStack stack) {
        NBTTagCompound nbt = UtilLib.checkNBT(stack);
        if (nbt.hasKey("type")) {
            try {
                return TYPE.valueOf(nbt.getString("type"));
            } catch (IllegalArgumentException e) {
                VampirismMod.log.e("HunterAxe", e, "Cannot find type %s", nbt.getString("type"));
            }
        }
        return TYPE.NORMAL;
    }

    /**
     * Set's the type of the stack
     *
     * @param stack
     * @param type
     * @return The same stack for chaining
     */
    public static ItemStack setType(ItemStack stack, TYPE type) {
        NBTTagCompound nbt = UtilLib.checkNBT(stack);
        nbt.setString("type", type.name());
        Map<Enchantment, Integer> map = new HashMap<>();
        map.put(Enchantments.KNOCKBACK, type.knockback);
        EnchantmentHelper.setEnchantments(map, stack);
        return stack;
    }

    public ItemHunterAxe() {
        super(regName, ToolMaterial.IRON, -2.5F);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
        super.addInformation(stack, playerIn, tooltip, advanced);
        TYPE type = getType(stack);
        if (type != TYPE.NORMAL) {
            tooltip.add(TextFormatting.AQUA + UtilLib.translateToLocal("text.vampirism.itemType." + type.name().toLowerCase()));

        }
        tooltip.add(UtilLib.translateToLocalFormatted("text.vampirism.deals_more_damage_to", Math.round((type.vampireMult - 1) * 100)) + " " + UtilLib.translateToLocal(VReference.VAMPIRE_FACTION.getUnlocalizedNamePlural()));
    }

    @Override
    public float getDamageMultiplierForFaction(ItemStack stack) {
        return getType(stack).vampireMult;
    }

    @Override
    public int getMinLevel(ItemStack stack) {
        return getType(stack).minLevel;
    }

    @Override
    public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {
        for (TYPE t : TYPE.values()) {
            subItems.add(setType(new ItemStack(itemIn), t));
        }
    }

    @Override
    public boolean hasEffect(ItemStack stack) {
        return false;
    }

    public enum TYPE implements IStringSerializable {
        NORMAL(1.1F, 2, 4), ENHANCED(1.3F, 3, 6), ULTIMATE(1.4F, 4, 8);
        public final float vampireMult;
        public final int knockback;
        public final int minLevel;

        TYPE(float vampireMult, int knockback, int minLevel) {
            this.vampireMult = vampireMult;
            this.knockback = knockback;
            this.minLevel = minLevel;
        }


        @Override
        public String getName() {
            return name().toLowerCase();
        }
    }


}

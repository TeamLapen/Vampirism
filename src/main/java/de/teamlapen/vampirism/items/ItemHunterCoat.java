package de.teamlapen.vampirism.items;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.items.IItemWithTier;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextFormatting;

import java.util.List;


public class ItemHunterCoat extends VampirismHunterArmor implements IItemWithTier {

    private final static String baseRegName = "hunterCoat";

    public static boolean isFullyEquipped(EntityPlayer player) {
        for (ItemStack stack : player.inventory.armorInventory) {
            if (stack == null || !(stack.getItem() instanceof ItemHunterCoat)) {
                return false;
            }
        }
        return true;
    }

    private final int[] DAMAGE_REDUCTION_ULTIMATE = new int[]{3, 6, 8, 3};
    private final int[] DAMAGE_REDUCTION_ENHANCED = new int[]{2, 6, 7, 2};
    private final int[] DAMAGE_REDUCTION_NORMAL = new int[]{2, 5, 6, 2};

    public ItemHunterCoat(EntityEquipmentSlot equipmentSlotIn) {
        super(ArmorMaterial.IRON, equipmentSlotIn, baseRegName);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
        super.addInformation(stack, playerIn, tooltip, advanced);
        TIER t = getTier(stack);
        if (t != TIER.NORMAL) {
            tooltip.add(TextFormatting.AQUA + UtilLib.translate("text.vampirism.itemTier." + t.name().toLowerCase()));
        }
    }


    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
        switch (getTier(stack)) {
            case ENHANCED:
                return getTextureLocation("hunter_coat_enhanced", slot, type);
            case ULTIMATE:
                return getTextureLocation("hunter_coat_enhanced", slot, type);
            default:
                return getTextureLocation("hunter_coat", slot, type);
        }

    }


    @Override
    public void getSubItems(Item itemIn, CreativeTabs tab, NonNullList<ItemStack> subItems) {
        for (TIER t : TIER.values()) {
            subItems.add(setTier(new ItemStack(itemIn), t));
        }
    }

    @Override
    public TIER getTier(ItemStack stack) {
        NBTTagCompound tag = UtilLib.checkNBT(stack);
        if (tag.hasKey("tier")) {
            try {
                return TIER.valueOf(tag.getString("tier"));
            } catch (IllegalArgumentException e) {
                VampirismMod.log.e("ArmorOfSwiftness", e, "Unknown item tier %s", tag.getString("tier"));
            }

        }
        return TIER.NORMAL;
    }

    @Override
    public ItemStack setTier(ItemStack stack, TIER tier) {
        NBTTagCompound tag = UtilLib.checkNBT(stack);
        tag.setString("tier", tier.name());
        return stack;
    }

    @Override
    protected int getDamageReduction(int slot, ItemStack stack) {
        TIER tier = getTier(stack);
        switch (tier) {
            case ULTIMATE:
                return DAMAGE_REDUCTION_ULTIMATE[slot];
            case ENHANCED:
                return DAMAGE_REDUCTION_ENHANCED[slot];
            default:
                return DAMAGE_REDUCTION_NORMAL[slot];
        }
    }


}

package de.teamlapen.vampirism.items;

import de.teamlapen.lib.lib.util.ItemStackUtil;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;


public class ItemHunterCoat extends VampirismHunterArmor implements IItemWithTierNBTImpl {

    private final static String baseRegName = "hunter_coat";

    public static boolean isFullyEquipped(EntityPlayer player) {
        for (ItemStack stack : player.inventory.armorInventory) {
            if (ItemStackUtil.isEmpty(stack) || !(stack.getItem() instanceof ItemHunterCoat)) {
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

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
        super.addInformation(stack, playerIn, tooltip, advanced);
        addTierInformation(stack, tooltip);
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

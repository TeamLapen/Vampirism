package de.teamlapen.vampirism.items;

import com.google.common.collect.Multimap;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;


public class ItemObsidianArmor extends VampirismHunterArmor implements IItemWithTierNBTImpl {

    private final static String baseRegName = "obsidian_armor";

    public static boolean isFullyEquipped(EntityPlayer player) {
        for (ItemStack stack : player.inventory.armorInventory) {
            if (stack.isEmpty() || !(stack.getItem() instanceof ItemObsidianArmor)) {
                return false;
            }
        }
        return true;
    }

    private final int[] DAMAGE_REDUCTION_ULTIMATE = new int[]{4, 7, 9, 4};
    private final int[] DAMAGE_REDUCTION_ENHANCED = new int[]{3, 7, 8, 3};
    private final int[] DAMAGE_REDUCTION_NORMAL = new int[]{3, 6, 7, 3};

    private final float[] SPEED_REDUCTION = new float[]{-0.025F, -0.1F, -0.05F, -0.025F};

    public ItemObsidianArmor(EntityEquipmentSlot equipmentSlotIn) {
        super(ArmorMaterial.IRON, equipmentSlotIn, baseRegName);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        addTierInformation(stack, tooltip);
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
        switch (getTier(stack)) {
            case ENHANCED:
                return getTextureLocation("obsidian_armor_of_hell_enhanced", slot, type);
            case ULTIMATE:
                return getTextureLocation("obsidian_armor_of_hell_ultimate", slot, type);
            default:
                return getTextureLocation("obsidian_armor_of_hell", slot, type);
        }

    }

    @Override
    public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack) {
        Multimap<String, AttributeModifier> map = super.getAttributeModifiers(slot, stack);
        if (slot == this.armorType) {
            map.put(SharedMonsterAttributes.MOVEMENT_SPEED.getName(), new AttributeModifier(VAMPIRISM_ARMOR_MODIFIER[slot.getIndex()], "Speed modifier", this.getSpeedReduction(slot.getIndex(), stack), 2));
        }
        return map;
    }


    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (isInCreativeTab(tab)) {
            for (TIER t : TIER.values()) {
                items.add(setTier(new ItemStack(this), t));
            }
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

    private float getSpeedReduction(int slot, ItemStack stack) {
        return SPEED_REDUCTION[slot];
    }
}

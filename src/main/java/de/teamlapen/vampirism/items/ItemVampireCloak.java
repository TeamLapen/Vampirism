package de.teamlapen.vampirism.items;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.client.model.ModelCloak;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Optioal create new enum type for cloak color
 *
 * @author cheaterpaul
 */
public class ItemVampireCloak extends ItemArmor {

    private final String registeredName = "vampire_cloak";

    public ItemVampireCloak() {
        super(ArmorMaterial.LEATHER, 0, EntityEquipmentSlot.CHEST);
        this.hasSubtypes = true;
        this.setMaxDamage(0);
        this.setCreativeTab(VampirismMod.creativeTab);
        this.setRegistryName(REFERENCE.MODID, registeredName);
        this.setUnlocalizedName(REFERENCE.MODID + "." + registeredName);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, EntityEquipmentSlot armorSlot, ModelBiped _default) {
        return new ModelCloak();
    }

    public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
        return String.format(REFERENCE.MODID + ":textures/models/armor/%s/%s_%s", registeredName, registeredName, EnumDyeColor.byMetadata(stack.getMetadata()).getDyeColorName() + ".png");
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems) {
        if (this.isInCreativeTab(tab))
            subItems.add(new ItemStack(this, 1, 15));
        if (tab.equals(CreativeTabs.SEARCH)) {
            for (EnumDyeColor s : EnumDyeColor.values()) {
                if (s.getMetadata() == 15)
                    continue;
                subItems.add(new ItemStack(this, 1, s.getMetadata()));
            }
        }

    }

    public String getUnlocalizedName(ItemStack stack) {
        int i = stack.getMetadata();
        return super.getUnlocalizedName() + "." + EnumDyeColor.byMetadata(i).getUnlocalizedName();
    }
}

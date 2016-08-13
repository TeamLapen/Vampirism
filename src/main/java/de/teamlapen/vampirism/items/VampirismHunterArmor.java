package de.teamlapen.vampirism.items;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.util.Helper;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.ISpecialArmor;

import java.util.List;
import java.util.UUID;

/**
 * Base class for all hunter only armor items
 */
abstract class VampirismHunterArmor extends ItemArmor implements ISpecialArmor {
    protected static final UUID[] ARMOR_MODIFIERS = new UUID[]{UUID.fromString("f0b9a417-0cec-4629-8623-053cd0feec3c"), UUID.fromString("e54474a9-62a0-48ee-baaf-7efddca3d711"), UUID.fromString("ac0c33f4-ebbf-44fe-9be3-a729f7633329"), UUID.fromString("8839e157-d576-4cff-bf34-0a788131fe0f")};

    public VampirismHunterArmor(ArmorMaterial materialIn, EntityEquipmentSlot equipmentSlotIn, String baseRegName) {
        super(materialIn, 0, equipmentSlotIn);
        setCreativeTab(VampirismMod.creativeTab);
        String regName = baseRegName + "_" + equipmentSlotIn.getName();
        setRegistryName(REFERENCE.MODID, regName);
        this.setUnlocalizedName(REFERENCE.MODID + "." + baseRegName + "." + equipmentSlotIn.getName());
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
        super.addInformation(stack, playerIn, tooltip, advanced);
        if (Helper.isVampire(playerIn)) {
            tooltip.add(TextFormatting.RED + UtilLib.translateToLocal("text.vampirism.poisonous_to_vampires"));
        }
    }

    @Override
    public void damageArmor(EntityLivingBase entity, ItemStack stack, DamageSource source, int damage, int slot) {
        stack.damageItem(damage, entity);
    }

    @Override
    public void onArmorTick(World world, EntityPlayer player, ItemStack itemStack) {
        super.onArmorTick(world, player, itemStack);
        if (player.ticksExisted % 16 == 8) {
            if (Helper.isVampire(player)) {
                player.addPotionEffect(new PotionEffect(MobEffects.POISON, 20, 1));
            }
        }
    }
}

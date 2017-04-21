package de.teamlapen.vampirism.items;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.util.Helper;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;
import java.util.UUID;

/**
 * Base class for all hunter only armor items
 */
public abstract class VampirismHunterArmor extends ItemArmor implements ISpecialArmor {
    protected static final UUID[] VAMPIRISM_ARMOR_MODIFIER = new UUID[]{UUID.fromString("f0b9a417-0cec-4629-8623-053cd0feec3c"), UUID.fromString("e54474a9-62a0-48ee-baaf-7efddca3d711"), UUID.fromString("ac0c33f4-ebbf-44fe-9be3-a729f7633329"), UUID.fromString("8839e157-d576-4cff-bf34-0a788131fe0f")};

    private final String registeredName, oldRegisteredName;

    public VampirismHunterArmor(ArmorMaterial materialIn, EntityEquipmentSlot equipmentSlotIn, String baseRegName) {
        super(materialIn, 0, equipmentSlotIn);
        setCreativeTab(VampirismMod.creativeTab);
        String regName = baseRegName + "_" + equipmentSlotIn.getName();
        setRegistryName(REFERENCE.MODID, regName);
        this.setUnlocalizedName(REFERENCE.MODID + "." + baseRegName + "." + equipmentSlotIn.getName());
        registeredName = regName;
        oldRegisteredName = baseRegName.replaceAll("_", "") + "_" + equipmentSlotIn.getName();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
        super.addInformation(stack, playerIn, tooltip, advanced);
        if (Helper.isVampire(playerIn)) {
            tooltip.add(TextFormatting.RED + UtilLib.translate("text.vampirism.poisonous_to_vampires"));
        }
    }

    @Override
    public void damageArmor(EntityLivingBase entity, ItemStack stack, DamageSource source, int damage, int slot) {
        stack.damageItem(damage, entity);
    }

    @Override
    public int getArmorDisplay(EntityPlayer player, ItemStack armor, int slot) {
        return getDamageReduction(slot, armor);
    }

    @Override
    public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack) {
        Multimap<String, AttributeModifier> map = HashMultimap.create();
        if (slot == this.armorType) {
            map.put(SharedMonsterAttributes.ARMOR.getName(), new AttributeModifier(VAMPIRISM_ARMOR_MODIFIER[slot.getIndex()], "Armor modifier", (double) this.getDamageReduction(slot.getIndex(), stack), 0));
            map.put(SharedMonsterAttributes.ARMOR_TOUGHNESS.getName(), new AttributeModifier(VAMPIRISM_ARMOR_MODIFIER[slot.getIndex()], "Armor toughness", this.getToughness(slot.getIndex(), stack), 0));
        }
        return map;
    }

    /**
     * @return The lowercase version of the string this armor was registered under 1.10
     */
    public String getOldRegisteredName() {
        return oldRegisteredName;
    }

    @Override
    public ArmorProperties getProperties(EntityLivingBase player, ItemStack armor, DamageSource source, double damage, int slot) {
        return new ArmorProperties(0, getDamageReduction(slot, armor) / 25D, Integer.MAX_VALUE);
    }

    /**
     * @return The name this armor piece is registered in the GameRegistry
     */
    public String getRegisteredName() {
        return registeredName;
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

    /**
     * @param stack Armor stack
     * @return The damage reduction the given stack gives
     */
    protected abstract int getDamageReduction(int slot, ItemStack stack);

    protected String getTextureLocation(String name, EntityEquipmentSlot slot, String type) {
        return String.format(REFERENCE.MODID + ":textures/models/armor/%s_layer_%d%s.png", name, slot == EntityEquipmentSlot.LEGS ? 2 : 1, type == null ? "" : "_overlay");
    }

    /**
     * @return The toughness of the given stack
     */
    protected double getToughness(int slot, ItemStack stack) {
        return this.toughness;
    }
}

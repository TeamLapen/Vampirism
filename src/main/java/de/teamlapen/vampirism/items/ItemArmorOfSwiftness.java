package de.teamlapen.vampirism.items;

import com.google.common.collect.Multimap;
import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import java.util.List;


public class ItemArmorOfSwiftness extends VampirismHunterArmor {

    private final static String baseRegName = "armorOfSwiftness";

    public static TYPE getType(ItemStack stack) {
        NBTTagCompound tag = UtilLib.checkNBT(stack);
        if (tag.hasKey("type")) {
            try {
                return TYPE.valueOf(tag.getString("type"));
            } catch (IllegalArgumentException e) {
                VampirismMod.log.e("ArmorOfSwiftness", e, "Invalid armor type specified in nbt");
            }

        }
        return TYPE.NORMAL;

    }

    /**
     * Set the swiftness armor type
     *
     * @param stack
     * @param type
     * @return the same stack for chaining
     */
    public static ItemStack setType(ItemStack stack, TYPE type) {
        NBTTagCompound tag = UtilLib.checkNBT(stack);
        tag.setString("type", type.name());
        return stack;
    }


    public ItemArmorOfSwiftness(EntityEquipmentSlot equipmentSlotIn) {
        super(ArmorMaterial.LEATHER, equipmentSlotIn, baseRegName);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
        super.addInformation(stack, playerIn, tooltip, advanced);
        TYPE t = getType(stack);
        if (t != TYPE.NORMAL) {
            tooltip.add(TextFormatting.AQUA + UtilLib.translateToLocal("text.vampirism.armor." + t.name().toLowerCase()));
        }
    }

    @Override
    public int getArmorDisplay(EntityPlayer player, ItemStack armor, int slot) {
        TYPE type = getType(armor);
        return type.damageReduction[slot];
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
        if (type == null) {
            return getTextureLocationLeather(slot);
        }
        switch (getType(stack)) {
            case ENHANCED:
                return getTextureLocation("swiftness_enhanced", slot, type);
            case ULTIMATE:
                return getTextureLocation("swiftness_ultimate", slot, type);
            default:
                return getTextureLocation("swiftness", slot, type);
        }
    }

    @Override
    public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot equipmentSlot, ItemStack stack) {
        Multimap<String, AttributeModifier> multimap = super.getAttributeModifiers(equipmentSlot, stack);

        if (equipmentSlot == this.armorType) {
            TYPE type = getType(stack);
            multimap.put(SharedMonsterAttributes.MOVEMENT_SPEED.getAttributeUnlocalizedName(), new AttributeModifier(ARMOR_MODIFIERS[equipmentSlot.getIndex()], "Armor Swiftness", type.speedBoost, 2));
        }

        return multimap;
    }

    @Override
    public int getItemEnchantability() {
        return 0;
    }

    @Override
    public ArmorProperties getProperties(EntityLivingBase player, ItemStack armor, DamageSource source, double damage, int slot) {
        TYPE type = getType(armor);
        return new ArmorProperties(0, type.damageReduction[slot] / 25D, Integer.MAX_VALUE);
    }

    @Override
    public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {
        for (TYPE t : TYPE.values()) {
            subItems.add(setType(new ItemStack(itemIn), t));
        }
    }

    @Override
    public boolean hasEffect(ItemStack stack) {
        return true;
    }

    @Override
    public void onArmorTick(World world, EntityPlayer player, ItemStack itemStack) {
        super.onArmorTick(world, player, itemStack);
        if (player.ticksExisted % 45 == 3) {
            if (this.armorType == EntityEquipmentSlot.CHEST) {
                boolean flag = true;
                int boost = Integer.MAX_VALUE;
                for (ItemStack stack : player.inventory.armorInventory) {
                    if (stack != null && stack.getItem() instanceof ItemArmorOfSwiftness) {
                        int b = getType(stack).jumpBoost;
                        if (b < boost) {
                            boost = b;
                        }
                    } else {
                        flag = false;
                        break;
                    }
                }
                if (flag && boost > -1) {
                    player.addPotionEffect(new PotionEffect(MobEffects.JUMP_BOOST, 50, boost));
                }
            }
        }
    }

    private String getTextureLocation(String name, EntityEquipmentSlot slot, String type) {
        return String.format(REFERENCE.MODID + ":textures/models/armor/%s_layer_%d%s.png", name, slot == EntityEquipmentSlot.LEGS ? 2 : 1, type == null ? "" : "_overlay");
    }

    private String getTextureLocationLeather(EntityEquipmentSlot slot) {
        return String.format("minecraft:textures/models/armor/leather_layer_%d.png", slot == EntityEquipmentSlot.LEGS ? 2 : 1);
    }

    public enum TYPE implements IStringSerializable {
        NORMAL(0.035, -1, new int[]{1, 2, 3, 1}), ENHANCED(0.075, 0, new int[]{2, 3, 4, 2}), ULTIMATE(0.1, 1, new int[]{2, 5, 6, 2});
        /**
         * Applied per piece
         */
        final double speedBoost;
        /**
         * Applied if complete armor is worn
         * -1 if none
         */
        final int jumpBoost;
        final int[] damageReduction;


        TYPE(double speedBoost, int jumpBoost, int[] damageReduction) {
            this.speedBoost = speedBoost;
            this.jumpBoost = jumpBoost;
            this.damageReduction = damageReduction;
        }


        @Override
        public String getName() {
            return name().toLowerCase();
        }
    }

}

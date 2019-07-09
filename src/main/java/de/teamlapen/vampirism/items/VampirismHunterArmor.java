package de.teamlapen.vampirism.items;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import de.teamlapen.vampirism.util.Helper;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * Base class for all hunter only armor items
 */
public abstract class VampirismHunterArmor extends ArmorItem {
    protected static final UUID[] VAMPIRISM_ARMOR_MODIFIER = new UUID[]{UUID.fromString("f0b9a417-0cec-4629-8623-053cd0feec3c"), UUID.fromString("e54474a9-62a0-48ee-baaf-7efddca3d711"), UUID.fromString("ac0c33f4-ebbf-44fe-9be3-a729f7633329"), UUID.fromString("8839e157-d576-4cff-bf34-0a788131fe0f")};


    private final String translation_key;

    public VampirismHunterArmor(String baseRegName, @Nullable String suffix, IArmorMaterial materialIn, EquipmentSlotType equipmentSlotIn, Item.Properties props) {
        super(materialIn, equipmentSlotIn, props);
        String regName = baseRegName + "_" + equipmentSlotIn.getName();
        if (suffix != null) regName += "_" + suffix;
        setRegistryName(REFERENCE.MODID, regName);
        translation_key = Util.makeTranslationKey("item", new ResourceLocation(REFERENCE.MODID, baseRegName + "_" + equipmentSlotIn.getName()));
    }

    @Override
    protected String getDefaultTranslationKey() {
        return translation_key;
    }

    @Override
    public Multimap<String, AttributeModifier> getAttributeModifiers(EquipmentSlotType slot, ItemStack stack) {
        Multimap<String, AttributeModifier> map = HashMultimap.create();
        if (slot == this.armorType) {
            map.put(SharedMonsterAttributes.ARMOR.getName(), new AttributeModifier(VAMPIRISM_ARMOR_MODIFIER[slot.getIndex()], "Armor modifier", (double) this.getDamageReduction(slot.getIndex(), stack), 0));
            map.put(SharedMonsterAttributes.ARMOR_TOUGHNESS.getName(), new AttributeModifier(VAMPIRISM_ARMOR_MODIFIER[slot.getIndex()], "Armor toughness", this.getToughness(slot.getIndex(), stack), 0));
        }
        return map;
    }



    @Override
    public void onArmorTick(ItemStack stack, World world, PlayerEntity player) {
        if (player.ticksExisted % 16 == 8) {
            if (Helper.isVampire(player)) {
                player.addPotionEffect(new EffectInstance(Effects.POISON, 20, 1));
            }
        }
    }

    /**
     * @param stack Armor stack
     * @return The damage reduction the given stack gives
     */
    protected abstract int getDamageReduction(int slot, ItemStack stack);


    protected String getTextureLocation(String name, EquipmentSlotType slot, String type) {
        return String.format(REFERENCE.MODID + ":textures/models/armor/%s_layer_%d%s.png", name, slot == EquipmentSlotType.LEGS ? 2 : 1, type == null ? "" : "_overlay");
    }

    /**
     * @return The toughness of the given stack
     */
    protected double getToughness(int slot, ItemStack stack) {
        return this.toughness;
    }
}

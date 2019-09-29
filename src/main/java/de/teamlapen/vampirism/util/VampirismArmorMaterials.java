package de.teamlapen.vampirism.util;

import net.minecraft.block.Blocks;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.LazyLoadBase;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.function.Supplier;


public enum VampirismArmorMaterials implements IArmorMaterial {
    OBSIDIAN("obsidian", 37, new int[]{2, 5, 6, 2}, 10, SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND, 2.0F, () -> {
        return Ingredient.fromItems(Blocks.OBSIDIAN);
    });

    private static final int[] MAX_DAMAGE_ARRAY = new int[]{13, 15, 16, 11};
    private final String name;
    private final int maxDamageFactor;
    private final int[] damageReductionAmountArray;
    private final int enchantability;
    private final SoundEvent soundEvent;
    private final float toughness;
    private final LazyLoadBase<Ingredient> repairMaterial;

    VampirismArmorMaterials(String name, int maxDamageFactor, int[] damageReductionArray, int enchantability, SoundEvent sound, float toughness, Supplier<Ingredient> repairMaterial) {
        this.name = name;
        this.maxDamageFactor = maxDamageFactor;
        this.damageReductionAmountArray = damageReductionArray;
        this.enchantability = enchantability;
        this.soundEvent = sound;
        this.toughness = toughness;
        this.repairMaterial = new LazyLoadBase<>(repairMaterial);
    }

    public int getDamageReductionAmount(EquipmentSlotType p_200902_1_) {
        return this.damageReductionAmountArray[p_200902_1_.getIndex()];
    }

    public int getDurability(EquipmentSlotType p_200896_1_) {
        return MAX_DAMAGE_ARRAY[p_200896_1_.getIndex()] * this.maxDamageFactor;
    }

    public int getEnchantability() {
        return this.enchantability;
    }

    @Nonnull
    @OnlyIn(Dist.CLIENT)
    public String getName() {
        return this.name;
    }

    public Ingredient getRepairMaterial() {
        return this.repairMaterial.getValue();
    }

    @Nonnull
    public SoundEvent getSoundEvent() {
        return this.soundEvent;
    }

    public float getToughness() {
        return this.toughness;
    }

}

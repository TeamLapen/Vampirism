package de.teamlapen.vampirism.util;

import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.LazyValue;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.function.Supplier;


public enum VampirismArmorMaterials implements IArmorMaterial {
    OBSIDIAN("obsidian", 37, new int[]{2, 5, 6, 2}, 10, SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND, 3.0F, 0.1f, () -> {
        return Ingredient.fromItems(Items.OBSIDIAN);
    });

    private static final int[] MAX_DAMAGE_ARRAY = new int[]{13, 15, 16, 11};
    private final String name;
    private final int maxDamageFactor;
    private final int[] damageReductionAmountArray;
    private final int enchantability;
    private final SoundEvent soundEvent;
    private final float toughness;
    private final float knockbackResistance;
    private final LazyValue<Ingredient> repairMaterial;

    VampirismArmorMaterials(String name, int maxDamageFactor, int[] damageReductionArray, int enchantability, SoundEvent sound, float toughness, float knockbackResistance, Supplier<Ingredient> repairMaterial) {
        this.name = name;
        this.maxDamageFactor = maxDamageFactor;
        this.damageReductionAmountArray = damageReductionArray;
        this.enchantability = enchantability;
        this.soundEvent = sound;
        this.toughness = toughness;
        this.knockbackResistance = knockbackResistance;
        this.repairMaterial = new LazyValue<>(repairMaterial);
    }

    public int getDamageReductionAmount(EquipmentSlotType slotIn) {
        return this.damageReductionAmountArray[slotIn.getIndex()];
    }

    public int getDurability(EquipmentSlotType slotIn) {
        return MAX_DAMAGE_ARRAY[slotIn.getIndex()] * this.maxDamageFactor;
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

    @Override
    public float getKnockbackResistance() {
        return this.knockbackResistance;
    }

}

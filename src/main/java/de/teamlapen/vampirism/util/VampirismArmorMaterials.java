package de.teamlapen.vampirism.util;

import de.teamlapen.vampirism.core.ModTags;
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

    MASTERLY_IRON("masterly_iron", 30, new int[]{2, 5, 6, 2}, 10, SoundEvents.ARMOR_EQUIP_IRON, 0, 0, () -> Ingredient.of(Items.IRON_INGOT)),
    MASTERLY_LEATHER("masterly_leather", 20, new int[]{1, 2, 3, 1}, 12, SoundEvents.ARMOR_EQUIP_LEATHER, 0.0F, 0.0F, () -> Ingredient.of(Items.LEATHER)),
    VAMPIRE_CLOTH("vampire_cloth",15, new int[]{1,2,3,1}, 15, SoundEvents.ARMOR_EQUIP_LEATHER, 0, 0, () -> Ingredient.of(ModTags.Items.HEART));

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

    public int getDefenseForSlot(EquipmentSlotType slotIn) {
        return this.damageReductionAmountArray[slotIn.getIndex()];
    }

    public int getDurabilityForSlot(EquipmentSlotType slotIn) {
        return MAX_DAMAGE_ARRAY[slotIn.getIndex()] * this.maxDamageFactor;
    }

    public int getEnchantmentValue() {
        return this.enchantability;
    }

    @Override
    public float getKnockbackResistance() {
        return this.knockbackResistance;
    }

    @Nonnull
    @OnlyIn(Dist.CLIENT)
    public String getName() {
        return this.name;
    }

    @Nonnull
    public SoundEvent getEquipSound() {
        return this.soundEvent;
    }

    public Ingredient getRepairIngredient() {
        return this.repairMaterial.get();
    }

    public float getToughness() {
        return this.toughness;
    }

}

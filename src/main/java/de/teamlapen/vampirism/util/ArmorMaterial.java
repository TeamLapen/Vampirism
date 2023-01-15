package de.teamlapen.vampirism.util;

import de.teamlapen.vampirism.api.items.IItemWithTier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class ArmorMaterial implements net.minecraft.world.item.ArmorMaterial {

    private static final int[] MAX_DAMAGE_ARRAY = new int[]{13, 15, 16, 11};

    private final String name;
    private final int maxDamageFactor;
    private final int[] damageReduction;
    private final int enchantability;
    private final SoundEvent soundEvent;
    private final float toughness;
    private final float knockbackResistance;
    private final Supplier<Ingredient> repairMaterial;

    public ArmorMaterial(String name, int maxDamageFactor, int[] damageReduction, int enchantability, SoundEvent soundEvent, float toughness, float knockbackResistance, Supplier<Ingredient> repairMaterial) {
        this.name = name;
        this.maxDamageFactor = maxDamageFactor;
        this.damageReduction = damageReduction;
        this.enchantability = enchantability;
        this.soundEvent = soundEvent;
        this.toughness = toughness;
        this.knockbackResistance = knockbackResistance;
        this.repairMaterial = repairMaterial;
    }

    @Override
    public int getDurabilityForSlot(@NotNull EquipmentSlot slot) {
        return MAX_DAMAGE_ARRAY[slot.getIndex()] * this.maxDamageFactor;
    }

    @Override
    public int getDefenseForSlot(@NotNull EquipmentSlot slot) {
        return this.damageReduction[slot.getIndex()];
    }

    @Override
    public int getEnchantmentValue() {
        return this.enchantability;
    }

    @Override
    public @NotNull SoundEvent getEquipSound() {
        return this.soundEvent;
    }

    @Override
    public @NotNull Ingredient getRepairIngredient() {
        return this.repairMaterial.get();
    }

    @Override
    public @NotNull String getName() {
        return this.name;
    }

    @Override
    public float getToughness() {
        return this.toughness;
    }

    @Override
    public float getKnockbackResistance() {
        return this.knockbackResistance;
    }

    public String name() {
        return this.name;
    }

    public static class Tiered extends ArmorMaterial {

        private final @NotNull IItemWithTier.TIER tier;

        public Tiered(String name, @NotNull IItemWithTier.TIER tier, int maxDamageFactor, int[] damageReduction, int enchantability, SoundEvent soundEvent, float toughness, float knockbackResistance, Supplier<Ingredient> repairMaterial) {
            super(name, maxDamageFactor, damageReduction, enchantability, soundEvent, toughness, knockbackResistance, repairMaterial);
            this.tier = tier;
        }

        public IItemWithTier.@NotNull TIER getTier() {
            return tier;
        }
    }

}

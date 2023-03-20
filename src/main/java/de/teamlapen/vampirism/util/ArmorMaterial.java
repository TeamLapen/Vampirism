package de.teamlapen.vampirism.util;

import de.teamlapen.vampirism.api.items.IItemWithTier;
import net.minecraft.Util;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;
import java.util.function.Supplier;

public class ArmorMaterial implements net.minecraft.world.item.ArmorMaterial {

    private static final EnumMap<ArmorItem.Type, Integer> HEALTH_FUNCTION_FOR_TYPE = Util.make(new EnumMap<>(ArmorItem.Type.class), (p_266653_) -> {
        p_266653_.put(ArmorItem.Type.BOOTS, 13);
        p_266653_.put(ArmorItem.Type.LEGGINGS, 15);
        p_266653_.put(ArmorItem.Type.CHESTPLATE, 16);
        p_266653_.put(ArmorItem.Type.HELMET, 11);
    });

    private final String name;
    private final int maxDamageFactor;
    private final EnumMap<ArmorItem.Type, Integer> damageReduction;
    private final int enchantability;
    private final SoundEvent soundEvent;
    private final float toughness;
    private final float knockbackResistance;
    private final Supplier<Ingredient> repairMaterial;

    public ArmorMaterial(String name, int maxDamageFactor, EnumMap<ArmorItem.Type, Integer> damageReduction, int enchantability, SoundEvent soundEvent, float toughness, float knockbackResistance, Supplier<Ingredient> repairMaterial) {
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
    public int getDurabilityForType(ArmorItem.@NotNull Type type) {
        return HEALTH_FUNCTION_FOR_TYPE.get(type) * this.maxDamageFactor;
    }

    @Override
    public int getDefenseForType(ArmorItem.@NotNull Type type) {
        return this.damageReduction.get(type);
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

        public Tiered(String name, @NotNull IItemWithTier.TIER tier, int maxDamageFactor, EnumMap<ArmorItem.Type, Integer> damageReduction, int enchantability, SoundEvent soundEvent, float toughness, float knockbackResistance, Supplier<Ingredient> repairMaterial) {
            super(name, maxDamageFactor, damageReduction, enchantability, soundEvent, toughness, knockbackResistance, repairMaterial);
            this.tier = tier;
        }

        public IItemWithTier.@NotNull TIER getTier() {
            return tier;
        }
    }

    public static EnumMap<ArmorItem.Type, Integer> createReduction(int helmet, int chestplate, int leggings, int boots) {
        return Util.make(new EnumMap<>(ArmorItem.Type.class), (map) -> {
            map.put(ArmorItem.Type.BOOTS, boots);
            map.put(ArmorItem.Type.LEGGINGS, leggings);
            map.put(ArmorItem.Type.CHESTPLATE, chestplate);
            map.put(ArmorItem.Type.HELMET, helmet);
        });
    }

}

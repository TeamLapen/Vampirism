package de.teamlapen.vampirism.util;

import de.teamlapen.vampirism.core.ModTags;
import net.minecraft.Util;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;
import java.util.function.Supplier;

import static de.teamlapen.vampirism.util.ArmorMaterial.createReduction;

/**
 * @deprecated use the specific materials in {@link de.teamlapen.vampirism.items.ArmorOfSwiftnessItem}, {@link de.teamlapen.vampirism.items.HunterCoatItem} and {@link de.teamlapen.vampirism.items.VampireClothingItem}
 */
@Deprecated(forRemoval = true, since = "1.9")
public enum VampirismArmorMaterials implements ArmorMaterial {
    MASTERLY_IRON("masterly_iron", 30, createReduction(2, 6,5, 2), 10, SoundEvents.ARMOR_EQUIP_IRON, 0, 0, () -> Ingredient.of(Tags.Items.INGOTS_IRON)),
    MASTERLY_LEATHER("masterly_leather", 20, createReduction(1, 3, 2,  1), 12, SoundEvents.ARMOR_EQUIP_LEATHER, 0.0F, 0.0F, () -> Ingredient.of(Tags.Items.LEATHER)),
    VAMPIRE_CLOTH("vampire_cloth", 5, createReduction(1, 3, 2,  1), 15, SoundEvents.ARMOR_EQUIP_LEATHER, 0, 0, () -> Ingredient.of(ModTags.Items.HEART));

    private static final EnumMap<ArmorItem.Type, Integer> HEALTH_FUNCTION_FOR_TYPE = Util.make(new EnumMap<>(ArmorItem.Type.class), (p_266653_) -> {
        p_266653_.put(ArmorItem.Type.BOOTS, 13);
        p_266653_.put(ArmorItem.Type.LEGGINGS, 15);
        p_266653_.put(ArmorItem.Type.CHESTPLATE, 16);
        p_266653_.put(ArmorItem.Type.HELMET, 11);
    });
    private final String name;
    private final int maxDamageFactor;
    private final EnumMap<ArmorItem.Type, Integer> damageReductionAmountArray;
    private final int enchantability;
    private final SoundEvent soundEvent;
    private final float toughness;
    private final float knockbackResistance;
    private final Supplier<Ingredient> repairMaterial;

    VampirismArmorMaterials(String name, int maxDamageFactor, EnumMap<ArmorItem.Type, Integer> damageReductionArray, int enchantability, SoundEvent sound, float toughness, float knockbackResistance, Supplier<Ingredient> repairMaterial) {
        this.name = name;
        this.maxDamageFactor = maxDamageFactor;
        this.damageReductionAmountArray = damageReductionArray;
        this.enchantability = enchantability;
        this.soundEvent = sound;
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
        return this.damageReductionAmountArray.get(type);
    }

    public int getEnchantmentValue() {
        return this.enchantability;
    }

    @Override
    public float getKnockbackResistance() {
        return this.knockbackResistance;
    }

    @NotNull
    public String getName() {
        return this.name;
    }

    @NotNull
    public SoundEvent getEquipSound() {
        return this.soundEvent;
    }

    @NotNull
    public Ingredient getRepairIngredient() {
        return this.repairMaterial.get();
    }

    public float getToughness() {
        return this.toughness;
    }

}

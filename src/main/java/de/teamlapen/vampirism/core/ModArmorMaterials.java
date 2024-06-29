package de.teamlapen.vampirism.core;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.util.VResourceLocation;
import de.teamlapen.vampirism.core.tags.ModItemTags;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class ModArmorMaterials {

    public static final DeferredRegister<ArmorMaterial> ARMOR_MATERIALS = DeferredRegister.create(Registries.ARMOR_MATERIAL, REFERENCE.MODID);

    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> VAMPIRE_CLOTH = register("vampire_cloth", createReduction(1, 3, 2, 1), 15, SoundEvents.ARMOR_EQUIP_LEATHER, () -> Ingredient.of(ModItemTags.HEART), 0, 0);
    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> VAMPIRE_CLOTH_CROWN = copyWithNewLayer("vampire_clothing_crown", VAMPIRE_CLOTH);
    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> VAMPIRE_CLOTH_HAT = copyWithNewLayer("vampire_clothing_hat", VAMPIRE_CLOTH);
    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> VAMPIRE_CLOTH_LEGS = copyWithNewLayer("vampire_clothing_legs", VAMPIRE_CLOTH);
    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> VAMPIRE_CLOTH_BOOTS = copyWithNewLayer("vampire_clothing_boots", VAMPIRE_CLOTH);
    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> VAMPIRE_CLOAK_BLACK_BLUE = copyWithNewLayer("vampire_cloak_black_blue", VAMPIRE_CLOTH);
    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> VAMPIRE_CLOAK_BLACK_RED = copyWithNewLayer("vampire_cloak_black_red", VAMPIRE_CLOTH);
    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> VAMPIRE_CLOAK_BLACK_WHITE = copyWithNewLayer("vampire_cloak_black_white", VAMPIRE_CLOTH);
    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> VAMPIRE_CLOAK_RED_BLACK = copyWithNewLayer("vampire_cloak_red_black", VAMPIRE_CLOTH);
    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> VAMPIRE_CLOAK_WHITE_BLACK = copyWithNewLayer("vampire_cloak_white_black", VAMPIRE_CLOTH);
    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> NORMAL_HUNTER_COAT = register("hunter_coat", createReduction(2, 6, 5, 2), 10, SoundEvents.ARMOR_EQUIP_IRON, () -> Ingredient.of(Tags.Items.INGOTS_IRON), 2, 0);
    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> ENHANCED_HUNTER_COAT = register("hunter_coat_enhanced", createReduction(3, 8, 6, 3), 10, SoundEvents.ARMOR_EQUIP_IRON, () -> Ingredient.of(Tags.Items.INGOTS_IRON), 2, 0);
    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> ULTIMATE_HUNTER_COAT = register("hunter_coat_ultimate", createReduction(3, 9, 9, 3), 10, SoundEvents.ARMOR_EQUIP_IRON, () -> Ingredient.of(Tags.Items.GEMS_DIAMOND), 2, 0);
    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> NORMAL_SWIFTNESS = register("armor_of_swiftness_normal", createReduction(1, 3, 2, 1), 12, SoundEvents.ARMOR_EQUIP_LEATHER, () -> Ingredient.of(Tags.Items.LEATHERS), 0, 0);
    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> ENHANCED_SWIFTNESS = register("armor_of_swiftness_enhanced", createReduction(2, 6, 5, 2), 12, SoundEvents.ARMOR_EQUIP_LEATHER, () -> Ingredient.of(Tags.Items.LEATHERS), 0, 0);
    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> ULTIMATE_SWIFTNESS = register("armor_of_swiftness_ultimate", createReduction(3, 8, 6, 3), 12, SoundEvents.ARMOR_EQUIP_LEATHER, () -> Ingredient.of(Tags.Items.LEATHERS), 0, 0);
    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> HUNTER_HAT_0 = copyWithNewLayer("hunter_hat_head_0", ArmorMaterials.IRON);
    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> HUNTER_HAT_1 = copyWithNewLayer("hunter_hat_head_1", ArmorMaterials.IRON);


    static void register(IEventBus bus) {
        ARMOR_MATERIALS.register(bus);
    }

    private static DeferredHolder<ArmorMaterial, ArmorMaterial> copyWithNewLayer(String name, Holder<ArmorMaterial> sourceHolder) {
        return ARMOR_MATERIALS.register(name, () -> {
            ArmorMaterial source = sourceHolder.value();
            var layers = List.of(new ArmorMaterial.Layer(VResourceLocation.mod(name)));
            return new ArmorMaterial(source.defense(), source.enchantmentValue(), source.equipSound(), source.repairIngredient(), layers, source.toughness(), source.knockbackResistance());
        });
    }

    private static DeferredHolder<ArmorMaterial, ArmorMaterial> register(String name, Map<ArmorItem.Type, Integer> defense, int enchantmentValue, Holder<SoundEvent> equipSound, Supplier<Ingredient> repairIngredient, float toughness, float knockbackResistance) {
        return ARMOR_MATERIALS.register(name, () -> {
            List<ArmorMaterial.Layer> list = List.of(new ArmorMaterial.Layer(VResourceLocation.mod(name)));
            return new ArmorMaterial(defense, enchantmentValue, equipSound, repairIngredient, list, toughness, knockbackResistance);
        });
    }

    private static DeferredHolder<ArmorMaterial, ArmorMaterial> registerWithOverlay(String name, Map<ArmorItem.Type, Integer> defense, int enchantmentValue, Holder<SoundEvent> equipSound, Supplier<Ingredient> repairIngredient, float toughness, float knockbackResistance) {
        return ARMOR_MATERIALS.register(name, () -> {
            List<ArmorMaterial.Layer> list = List.of(new ArmorMaterial.Layer(VResourceLocation.mod(name), "", true), new ArmorMaterial.Layer(VResourceLocation.mod(name), "_overlay", false));
            return new ArmorMaterial(defense, enchantmentValue, equipSound, repairIngredient, list, toughness, knockbackResistance);
        });
    }

    private static EnumMap<ArmorItem.Type, Integer> createReduction(int helmet, int chestplate, int leggings, int boots) {
        return Util.make(new EnumMap<>(ArmorItem.Type.class), (map) -> {
            map.put(ArmorItem.Type.BOOTS, boots);
            map.put(ArmorItem.Type.LEGGINGS, leggings);
            map.put(ArmorItem.Type.CHESTPLATE, chestplate);
            map.put(ArmorItem.Type.HELMET, helmet);
        });
    }


}

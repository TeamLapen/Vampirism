package de.teamlapen.vampirism.core;

import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.items.IWeaponTableRecipe;
import de.teamlapen.vampirism.recipes.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * Handles all recipe registrations and reference.
 */
@SuppressWarnings("unused")
public class ModRecipes {
    private static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(Registries.RECIPE_TYPE, REFERENCE.MODID);
    private static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, REFERENCE.MODID);
    private static final DeferredRegister<Codec<? extends ICondition>> CONDITION_CODECS = DeferredRegister.create(NeoForgeRegistries.Keys.CONDITION_CODECS, REFERENCE.MODID);

    public static final DeferredHolder<RecipeType<?>, RecipeType<IWeaponTableRecipe>> WEAPONTABLE_CRAFTING_TYPE = RECIPE_TYPES.register("weapontable_crafting", () -> new RecipeType<>() {
        public @NotNull String toString() {
            return "weapontable_crafting";
        }
    });
    public static final DeferredHolder<RecipeType<?>, RecipeType<AlchemicalCauldronRecipe>> ALCHEMICAL_CAULDRON_TYPE = RECIPE_TYPES.register("alchemical_cauldron", () -> new RecipeType<>() {
        public @NotNull String toString() {
            return "alchemical_cauldron";
        }
    });
    public static final DeferredHolder<RecipeType<?>, RecipeType<AlchemyTableRecipe>> ALCHEMICAL_TABLE_TYPE = RECIPE_TYPES.register("alchemical_table", () -> new RecipeType<>() {
        public @NotNull String toString() {
            return "alchemical_table";
        }
    });

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<ShapedWeaponTableRecipe>> SHAPED_CRAFTING_WEAPONTABLE = RECIPE_SERIALIZERS.register("shaped_crafting_weapontable", ShapedWeaponTableRecipe.Serializer::new);
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<ShapelessWeaponTableRecipe>> SHAPELESS_CRAFTING_WEAPONTABLE = RECIPE_SERIALIZERS.register("shapeless_crafting_weapontable", ShapelessWeaponTableRecipe.Serializer::new);
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<ShapedRecipe>> REPAIR_IITEMWITHTIER = RECIPE_SERIALIZERS.register("repair_iitemwithtier", ShapedItemWithTierRepair.Serializer::new);
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<AlchemicalCauldronRecipe>> ALCHEMICAL_CAULDRON = RECIPE_SERIALIZERS.register("alchemical_cauldron", AlchemicalCauldronRecipe.Serializer::new);
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<AlchemyTableRecipe>> ALCHEMICAL_TABLE = RECIPE_SERIALIZERS.register("alchemical_table", AlchemyTableRecipe.Serializer::new);
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<ApplicableOilRecipe>> APPLICABLE_OIL = RECIPE_SERIALIZERS.register("applicable_oil", () -> new SimpleCraftingRecipeSerializer<>(ApplicableOilRecipe::new));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<CleanOilRecipe>> CLEAN_OIL = RECIPE_SERIALIZERS.register("clean_oil", () -> new SimpleCraftingRecipeSerializer<>(CleanOilRecipe::new));

    public static final DeferredHolder<Codec<? extends ICondition>, Codec<ConfigCondition>> CONFIG_CONDITION = CONDITION_CODECS.register("config", () -> ConfigCondition.CODEC);

    private static final Map<Item, Integer> liquidColors = Maps.newHashMap();
    private static final Map<TagKey<Item>, Integer> liquidColorsTags = Maps.newHashMap();

    static void registerDefaultLiquidColors() {
        registerLiquidColor(ModItems.HOLY_WATER_BOTTLE_NORMAL.get(), 0x6666FF);
        registerLiquidColor(ModItems.HOLY_WATER_BOTTLE_ENHANCED.get(), 0x6666FF);
        registerLiquidColor(ModItems.HOLY_WATER_BOTTLE_ULTIMATE.get(), 0x6666FF);
        registerLiquidColor(ModItems.PURE_BLOOD_0.get(), 0x7c0805);
        registerLiquidColor(ModItems.PURE_BLOOD_1.get(), 0x7d0503);
        registerLiquidColor(ModItems.PURE_BLOOD_2.get(), 0x830000);
        registerLiquidColor(ModItems.PURE_BLOOD_3.get(), 0x7e0e0e);
        registerLiquidColor(ModItems.PURE_BLOOD_4.get(), 0x8e0000);

        registerLiquidColor(ModTags.Items.GARLIC, 0xBBBBBB);

    }

    static void register(@NotNull IEventBus bus) {
        RECIPE_TYPES.register(bus);
        RECIPE_SERIALIZERS.register(bus);
        CONDITION_CODECS.register(bus);
    }

    public static void registerLiquidColor(Item item, int color) {
        liquidColors.put(item, color);
    }

    public static void registerLiquidColor(TagKey<Item> items, int color) {
        liquidColorsTags.put(items, color);
    }

    /**
     * gets liquid color for item
     */
    public static int getLiquidColor(ItemStack stack) {
        Integer c = liquidColors.get(stack.getItem());
        if (c != null) return c;
        for (Map.Entry<TagKey<Item>, Integer> entry : liquidColorsTags.entrySet()) {
            if(stack.is(entry.getKey())) {
                return entry.getValue();
            }
        }

        return 0x00003B;
    }

}

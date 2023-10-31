package de.teamlapen.vampirism.core;

import com.google.common.collect.Maps;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.items.IWeaponTableRecipe;
import de.teamlapen.vampirism.recipes.*;
import de.teamlapen.vampirism.util.NBTIngredient;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * Handles all recipe registrations and reference.
 */
@SuppressWarnings("unused")
public class ModRecipes {
    public static @NotNull DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(ForgeRegistries.Keys.RECIPE_TYPES, REFERENCE.MODID);
    public static @NotNull DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, REFERENCE.MODID);

    public static final RegistryObject<RecipeType<IWeaponTableRecipe>> WEAPONTABLE_CRAFTING_TYPE = RECIPE_TYPES.register("weapontable_crafting", () -> new RecipeType<>() {
        public @NotNull String toString() {
            return "weapontable_crafting";
        }
    });
    public static final RegistryObject<RecipeType<AlchemicalCauldronRecipe>> ALCHEMICAL_CAULDRON_TYPE = RECIPE_TYPES.register("alchemical_cauldron", () -> new RecipeType<>() {
        public @NotNull String toString() {
            return "alchemical_cauldron";
        }
    });
    public static final RegistryObject<RecipeType<AlchemyTableRecipe>> ALCHEMICAL_TABLE_TYPE = RECIPE_TYPES.register("alchemical_table", () -> new RecipeType<>() {
        public @NotNull String toString() {
            return "alchemical_table";
        }
    });

    public static final RegistryObject<RecipeSerializer<ShapedWeaponTableRecipe>> SHAPED_CRAFTING_WEAPONTABLE = RECIPE_SERIALIZERS.register("shaped_crafting_weapontable", ShapedWeaponTableRecipe.Serializer::new);
    public static final RegistryObject<RecipeSerializer<ShapelessWeaponTableRecipe>> SHAPELESS_CRAFTING_WEAPONTABLE = RECIPE_SERIALIZERS.register("shapeless_crafting_weapontable", ShapelessWeaponTableRecipe.Serializer::new);
    public static final RegistryObject<RecipeSerializer<ShapedRecipe>> REPAIR_IITEMWITHTIER = RECIPE_SERIALIZERS.register("repair_iitemwithtier", ShapedItemWithTierRepair.Serializer::new);
    public static final RegistryObject<RecipeSerializer<AlchemicalCauldronRecipe>> ALCHEMICAL_CAULDRON = RECIPE_SERIALIZERS.register("alchemical_cauldron", AlchemicalCauldronRecipe.Serializer::new);
    public static final RegistryObject<RecipeSerializer<AlchemyTableRecipe>> ALCHEMICAL_TABLE = RECIPE_SERIALIZERS.register("alchemical_table", AlchemyTableRecipe.Serializer::new);
    public static final RegistryObject<RecipeSerializer<ApplicableOilRecipe>> APPLICABLE_OIL = RECIPE_SERIALIZERS.register("applicable_oil", () -> new SimpleCraftingRecipeSerializer<>(ApplicableOilRecipe::new));
    public static final RegistryObject<RecipeSerializer<CleanOilRecipe>> CLEAN_OIL = RECIPE_SERIALIZERS.register("clean_oil", () -> new SimpleCraftingRecipeSerializer<>(CleanOilRecipe::new));


    public static final IConditionSerializer<?> CONFIG_CONDITION = CraftingHelper.register(new ConfigCondition.Serializer());

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
        bus.addListener(ModRecipes::registerRecipeSerializers);
    }

    public static void registerLiquidColor(Item item, int color) {
        liquidColors.put(item, color);
    }

    public static void registerLiquidColor(TagKey<Item> items, int color) {
        liquidColorsTags.put(items, color);
    }

    static void registerRecipeSerializers(@NotNull RegisterEvent event) {
        if (event.getRegistryKey() == ForgeRegistries.Keys.RECIPE_SERIALIZERS) {
            CraftingHelper.register(new ResourceLocation(REFERENCE.MODID, "nbt"), NBTIngredient.Serializer.INSTANCE);
        }
    }

    /**
     * gets liquid color for item
     */
    public static int getLiquidColor(Item stack) {
        Integer c = liquidColors.get(stack);
        if (c != null) return c;
        for (Map.Entry<TagKey<Item>, Integer> entry : liquidColorsTags.entrySet()) {
            //noinspection ConstantConditions
            if (ForgeRegistries.ITEMS.tags().getTag(entry.getKey()).contains(stack)) {
                return entry.getValue();
            }
        }

        return 0x00003B;
    }

}

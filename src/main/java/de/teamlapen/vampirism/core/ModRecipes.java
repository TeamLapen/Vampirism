package de.teamlapen.vampirism.core;

import com.google.common.collect.Maps;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.items.IWeaponTableRecipe;
import de.teamlapen.vampirism.inventory.recipes.*;
import net.minecraft.core.Registry;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Map;

/**
 * Handles all recipe registrations and reference.
 */
@SuppressWarnings("unused")
public class ModRecipes {
    public static DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(Registry.RECIPE_TYPE_REGISTRY, REFERENCE.MODID);
    public static DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, REFERENCE.MODID);

    public static final RegistryObject<RecipeType<IWeaponTableRecipe>> WEAPONTABLE_CRAFTING_TYPE =
            RECIPE_TYPES.register("weapontable_crafting", () -> new RecipeType<IWeaponTableRecipe>() {
                public String toString() {
                    return "weapontable_crafting";
                }
            });
    public static final RegistryObject<RecipeType<AlchemicalCauldronRecipe>> ALCHEMICAL_CAULDRON_TYPE =
            RECIPE_TYPES.register("alchemical_cauldron", () -> new RecipeType<AlchemicalCauldronRecipe>() {
                public String toString() {
                    return "alchemical_cauldron";
                }
            });

    public static final RegistryObject<RecipeSerializer<ShapedWeaponTableRecipe>> shaped_crafting_weapontable;
    public static final RegistryObject<RecipeSerializer<ShapelessWeaponTableRecipe>> shapeless_crafting_weapontable;
    public static final RegistryObject<RecipeSerializer<ShapedRecipe>> repair_iitemwithtier;
    public static final RegistryObject<RecipeSerializer<AlchemicalCauldronRecipe>> alchemical_cauldron;

    public static final IConditionSerializer<?> CONFIG_CONDITION = CraftingHelper.register(new ConfigCondition.Serializer());

    private static final Map<Item, Integer> liquidColors = Maps.newHashMap();
    private static final Map<TagKey<Item>, Integer> liquidColorsTags = Maps.newHashMap();

    static void registerDefaultLiquidColors() {
        registerLiquidColor(ModItems.holy_water_bottle_normal.get(), 0x6666FF);
        registerLiquidColor(ModItems.holy_water_bottle_enhanced.get(), 0x6666FF);
        registerLiquidColor(ModItems.holy_water_bottle_ultimate.get(), 0x6666FF);

        registerLiquidColor(ModTags.Items.GARLIC, 0xBBBBBB);

    }

    static void registerRecipeTypesAndSerializers(IEventBus bus) {
        RECIPE_TYPES.register(bus);
        RECIPE_SERIALIZERS.register(bus);
    }

    static {
        shaped_crafting_weapontable = RECIPE_SERIALIZERS.register("shaped_crafting_weapontable", ShapedWeaponTableRecipe.Serializer::new);
        shapeless_crafting_weapontable = RECIPE_SERIALIZERS.register("shapeless_crafting_weapontable", ShapelessWeaponTableRecipe.Serializer::new);
        repair_iitemwithtier = RECIPE_SERIALIZERS.register("repair_iitemwithtier", ShapedItemWithTierRepair.Serializer::new);
        alchemical_cauldron = RECIPE_SERIALIZERS.register("alchemical_cauldron", AlchemicalCauldronRecipe.Serializer::new);
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
    public static int getLiquidColor(Item stack) {
        Integer c = liquidColors.get(stack);
        if (c != null) return c;
        for (Map.Entry<TagKey<Item>, Integer> entry : liquidColorsTags.entrySet()) {
            if (stack.builtInRegistryHolder().is(entry.getKey())) {
                return entry.getValue();
            }
        }

        return 0x505050;
    }

}

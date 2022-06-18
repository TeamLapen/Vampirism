package de.teamlapen.vampirism.core;

import com.google.common.collect.Maps;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.items.IWeaponTableRecipe;
import de.teamlapen.vampirism.inventory.recipes.*;
import de.teamlapen.vampirism.util.NBTIngredient;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.*;
import net.minecraft.tags.ITag;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Map;
import java.util.function.Supplier;

/**
 * Handles all recipe registrations and reference.
 */
@SuppressWarnings("unused")
public class ModRecipes {
    public static final DeferredRegister<IRecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, REFERENCE.MODID);
    public static final IRecipeType<IWeaponTableRecipe> WEAPONTABLE_CRAFTING_TYPE = IRecipeType.register(new ResourceLocation(REFERENCE.MODID, "weapontable_crafting").toString());
    public static final IRecipeType<AlchemicalCauldronRecipe> ALCHEMICAL_CAULDRON_TYPE = IRecipeType.register(new ResourceLocation(REFERENCE.MODID, "alchemical_cauldron").toString());
    public static final IRecipeType<AlchemyTableRecipe> ALCHEMICAL_TABLE_TYPE = IRecipeType.register(new ResourceLocation(REFERENCE.MODID, "alchemical_table").toString());

    public static final RegistryObject<IRecipeSerializer<ShapedWeaponTableRecipe>> SHAPED_CRAFTING_WEAPONTABLE = register("shaped_crafting_weapontable", ShapedWeaponTableRecipe.Serializer::new);
    public static final RegistryObject<IRecipeSerializer<ShapelessWeaponTableRecipe>> SHAPELESS_CRAFTING_WEAPONTABLE = register("shapeless_crafting_weapontable", ShapelessWeaponTableRecipe.Serializer::new);
    public static final RegistryObject<IRecipeSerializer<ShapedRecipe>> REPAIR_IITEMWITHTIER = register("repair_iitemwithtier", ShapedItemWithTierRepair.Serializer::new);
    public static final RegistryObject<IRecipeSerializer<AlchemicalCauldronRecipe>> ALCHEMICAL_CAULDRON = register("alchemical_cauldron", AlchemicalCauldronRecipe.Serializer::new);
    public static final RegistryObject<IRecipeSerializer<AlchemyTableRecipe>> ALCHEMICAL_TABLE = register("alchemical_table", AlchemyTableRecipe.Serializer::new);
    public static final RegistryObject<SpecialRecipeSerializer<ApplicableOilRecipe>> APPLICABLE_OIL = register("applicable_oil", () -> new SpecialRecipeSerializer<>(ApplicableOilRecipe::new));

    public static final IConditionSerializer<?> CONFIG_CONDITION = CraftingHelper.register(new ConfigCondition.Serializer());

    private static final Map<Item, Integer> liquidColors = Maps.newHashMap();
    private static final Map<ITag<Item>, Integer> liquidColorsTags = Maps.newHashMap();

    static void registerDefaultLiquidColors() {
        registerLiquidColor(ModItems.HOLY_WATER_BOTTLE_NORMAL.get(), 0x6666FF);
        registerLiquidColor(ModItems.HOLY_WATER_BOTTLE_ENHANCED.get(), 0x6666FF);
        registerLiquidColor(ModItems.HOLY_WATER_BOTTLE_ULTIMATE.get(), 0x6666FF);

        registerLiquidColor(ModTags.Items.GARLIC, 0xBBBBBB);

    }

    static void registerRecipeTypesAndSerializers(IEventBus bus) {
        RECIPE_SERIALIZERS.register(bus);
        CraftingHelper.register(new ResourceLocation(REFERENCE.MODID, "item"), NBTIngredient.Serializer.INSTANCE);
    }

    private static <T extends IRecipe<?>> RegistryObject<IRecipeSerializer<T>> register(String name, Supplier<? extends IRecipeSerializer<T>> sup) {
        return RECIPE_SERIALIZERS.register(name, sup);
    }

    public static void registerLiquidColor(Item item, int color) {
        liquidColors.put(item, color);
    }

    public static void registerLiquidColor(ITag<Item> items, int color) {
        liquidColorsTags.put(items, color);
    }

    /**
     * gets liquid color for item
     */
    public static int getLiquidColor(Item stack) {
        Integer c = liquidColors.get(stack);
        if (c != null) return c;
        for (Map.Entry<ITag<Item>, Integer> entry : liquidColorsTags.entrySet()) {
            if (entry.getKey().contains(stack)) {
                return entry.getValue();
            }
        }

        return 0x505050;
    }

}

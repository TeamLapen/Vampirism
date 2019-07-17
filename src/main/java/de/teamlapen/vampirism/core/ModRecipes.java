package de.teamlapen.vampirism.core;

import com.google.common.collect.Maps;

import de.teamlapen.vampirism.api.items.IWeaponTableRecipe;
import de.teamlapen.vampirism.inventory.recipes.*;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IConditionSerializer;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Map;

/**
 * Handles all recipe registrations and reference.
 */
public class ModRecipes {
    public static final IRecipeType<IWeaponTableRecipe> WEAPONTABLE_CRAFTING_TYPE = IRecipeType.register(new ResourceLocation(REFERENCE.MODID, "weapontable_crafting").toString());
    public static final IRecipeType<AlchemicalCauldronRecipe> ALCHEMICAL_CAULDRON_TYPE = IRecipeType.register(new ResourceLocation(REFERENCE.MODID, "alchemical_cauldron").toString());

    public static final IRecipeSerializer<ShapedWeaponTableRecipe> SHAPED_CRAFTING_WEAPONTABLE = IRecipeSerializer.register(new ResourceLocation(REFERENCE.MODID, "shaped_weapon_table_recipe").toString(), new ShapedWeaponTableRecipe.Serializer());
    public static final IRecipeSerializer<ShapelessWeaponTableRecipe> SHAPELESS_CRAFTING_WEAPONTABLE = IRecipeSerializer.register(new ResourceLocation(REFERENCE.MODID, "shapeless_weapon_table_recipe").toString(), new ShapelessWeaponTableRecipe.Serializer());
    public static final IRecipeSerializer<ShapedRecipe> REPAIR_IITEMWITHTIER = IRecipeSerializer.register(new ResourceLocation(REFERENCE.MODID, "shaped_item_with_tier_repair").toString(), new ShapedItemWithTierRepair.Serializer());
    public static final IRecipeSerializer<AlchemicalCauldronRecipe> ALCHEMICAL_CAULDRON = IRecipeSerializer.register(new ResourceLocation(REFERENCE.MODID, "alchemical_cauldron_recipe").toString(), new AlchemicalCauldronRecipe.Serializer());

    public static final IConditionSerializer CONFIG_CONDITION = CraftingHelper.register(new ResourceLocation(REFERENCE.MODID, "config_condition"), new ConfigEntryConditionSerializer());

    private static final Map<Object, Integer> liquidColors = Maps.newHashMap();

    public static void init() {
        registerLiquidColor(ModItems.holy_water_bottle_normal, 0x6666FF);
        registerLiquidColor(ModItems.holy_water_bottle_enhanced, 0x6666FF);
        registerLiquidColor(ModItems.holy_water_bottle_ultimate, 0x6666FF);

        registerLiquidColor(ModItems.item_garlic, 0xBBBBBB);

        for (Fluid fluid : ForgeRegistries.FLUIDS.getValues()) {
            registerLiquidColor(fluid, 0xFFFFFF);
        }
    }

    public static void registerLiquidColor(Item stack, int color) {
        liquidColors.put(stack, color);
    }

    public static void registerLiquidColor(Fluid fluid, int color) {
        liquidColors.put(fluid, color);
    }

    /**
     * gets liquid color for item or fluid
     *
     * @param stack should be item or fluid
     * @return
     */
    public static int getLiquidColor(Object stack) {
        return liquidColors.get(stack);
    }

}

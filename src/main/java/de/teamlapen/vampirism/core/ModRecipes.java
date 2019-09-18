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
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

import java.util.Map;

import static de.teamlapen.lib.lib.util.UtilLib.getNull;

/**
 * Handles all recipe registrations and reference.
 */
public class ModRecipes {
    public static final IRecipeType<IWeaponTableRecipe> WEAPONTABLE_CRAFTING_TYPE = IRecipeType.register(new ResourceLocation(REFERENCE.MODID, "weapontable_crafting").toString());
    public static final IRecipeType<AlchemicalCauldronRecipe> ALCHEMICAL_CAULDRON_TYPE = IRecipeType.register(new ResourceLocation(REFERENCE.MODID, "alchemical_cauldron").toString());

    @ObjectHolder(REFERENCE.MODID + ":shaped_crafting_weapontable")
    public static final IRecipeSerializer<ShapedWeaponTableRecipe> shaped_crafting_weapontable = getNull();
    @ObjectHolder(REFERENCE.MODID + ":shapeless_crafting_weapontable")
    public static final IRecipeSerializer<ShapelessWeaponTableRecipe> shapeless_crafting_weapontable = getNull();
    @ObjectHolder(REFERENCE.MODID + ":repair_iitemwithtier")
    public static final IRecipeSerializer<ShapedRecipe> repair_iitemwithtier = getNull();
    @ObjectHolder(REFERENCE.MODID + ":alchemical_cauldron")
    public static final IRecipeSerializer<AlchemicalCauldronRecipe> alchemical_cauldron = getNull();

    public static final IConditionSerializer<?> CONFIG_CONDITION = CraftingHelper.register(AutoConvertGlassBottleCondition.Serializer.INSTANCE);

    private static final Map<Object, Integer> liquidColors = Maps.newHashMap();

    public static void init() {
        registerLiquidColor(ModItems.holy_water_bottle_normal, 0x6666FF);
        registerLiquidColor(ModItems.holy_water_bottle_enhanced, 0x6666FF);
        registerLiquidColor(ModItems.holy_water_bottle_ultimate, 0x6666FF);

        registerLiquidColor(ModItems.item_garlic, 0xBBBBBB);

        for (Fluid fluid : ForgeRegistries.FLUIDS.getValues()) {//TODO 1.14 remove and check somewhere else if the item is a fluid (fluid.getAttributes().getColor())
            registerLiquidColor(fluid, fluid.getAttributes().getColor());
        }
    }

    static void registerSerializer(IForgeRegistry<IRecipeSerializer<?>> registry) {
        registry.register(new ShapedWeaponTableRecipe.Serializer().setRegistryName(REFERENCE.MODID, "shaped_crafting_weapontable"));
        registry.register(new ShapelessWeaponTableRecipe.Serializer().setRegistryName(REFERENCE.MODID, "shapeless_crafting_weapontable"));
        registry.register(new ShapedItemWithTierRepair.Serializer().setRegistryName(REFERENCE.MODID, "repair_iitemwithtier"));
        registry.register(new AlchemicalCauldronRecipe.Serializer().setRegistryName(REFERENCE.MODID, "alchemical_cauldron"));
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

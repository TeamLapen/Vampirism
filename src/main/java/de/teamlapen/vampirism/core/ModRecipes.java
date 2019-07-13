package de.teamlapen.vampirism.core;

import de.teamlapen.vampirism.api.items.IAlchemicalCauldronRecipe;
import de.teamlapen.vampirism.api.items.IWeaponTableRecipe;
import de.teamlapen.vampirism.inventory.crafting.ConfigEntryConditionSerializer;
import de.teamlapen.vampirism.recipes.ShapedItemWithTierRepair;
import de.teamlapen.vampirism.recipes.ShapedWeaponTableRecipe;
import de.teamlapen.vampirism.recipes.ShapelessWeaponTableRecipe;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IConditionSerializer;

/**
 * Handles all recipe registrations and reference.
 */
public class ModRecipes {
    public static final IRecipeType<IWeaponTableRecipe> WEAPONTABLE_CRAFTING_TYPE = IRecipeType.register(new ResourceLocation(REFERENCE.MODID, "weapontable_crafting").toString());
    public static final IRecipeType<IAlchemicalCauldronRecipe> ALCHEMICAL_CAULDRON_TYPE = IRecipeType.register(new ResourceLocation(REFERENCE.MODID, "alchemical_cauldron").toString());

    public static final IRecipeSerializer<ShapedWeaponTableRecipe> SHAPED_CRAFTING_WEAPONTABLE = IRecipeSerializer.register(new ResourceLocation(REFERENCE.MODID, "shaped_weapon_table_recipe").toString(), new ShapedWeaponTableRecipe.Serializer());
    public static final IRecipeSerializer<ShapelessWeaponTableRecipe> SHAPELESS_CRAFTING_WEAPONTABLE = IRecipeSerializer.register(new ResourceLocation(REFERENCE.MODID, "shapeless_weapon_table_recipe").toString(), new ShapelessWeaponTableRecipe.Serializer());
    public static final IRecipeSerializer<ShapedRecipe> REPAIR_IITEMWITHTIER = IRecipeSerializer.register(new ResourceLocation(REFERENCE.MODID, "shaped_item_with_tier_repair").toString(), new ShapedItemWithTierRepair.Serializer());

    public static final IConditionSerializer CONFIG_CONDITION = CraftingHelper.register(new ResourceLocation(REFERENCE.MODID, "config_condition"), new ConfigEntryConditionSerializer());

    public static void init() {
    }

}

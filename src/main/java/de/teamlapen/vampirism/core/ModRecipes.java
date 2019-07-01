package de.teamlapen.vampirism.core;

import de.teamlapen.vampirism.inventory.crafting.ConfigEntryConditionSerializer;
import de.teamlapen.vampirism.recipes.ShapedItemWithTierRepair;
import de.teamlapen.vampirism.recipes.ShapedWeaponTableRecipe;
import de.teamlapen.vampirism.recipes.ShapelessWeaponTableRecipe;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.RecipeSerializers;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IConditionSerializer;
import net.minecraftforge.common.crafting.RecipeType;

/**
 * Handles all recipe registrations and reference.
 */
public class ModRecipes {
    public static final RecipeType<IRecipe> WEAPONTABLE_CRAFTING_TYPE = RecipeType.get(new ResourceLocation(REFERENCE.MODID, "weapontable_crafting"), IRecipe.class);
    public static final IRecipeSerializer<ShapedWeaponTableRecipe> SHAPED_CRAFTING_WEAPONTABLE = RecipeSerializers.register(new ShapedWeaponTableRecipe.Serializer());
    public static final IRecipeSerializer<ShapelessWeaponTableRecipe> SHAPELESS_CRAFTING_WEAPONTABLE = RecipeSerializers.register(new ShapelessWeaponTableRecipe.Serializer());
    public static final IRecipeSerializer<ShapedRecipe> REPAIR_IITEMWITHTIER = RecipeSerializers.register(new ShapedItemWithTierRepair.Serializer());
    public static final IConditionSerializer CONFIG_CONDITION = CraftingHelper.register(new ResourceLocation(REFERENCE.MODID, "config_condition"), new ConfigEntryConditionSerializer());

    public static void init() {
    }

}

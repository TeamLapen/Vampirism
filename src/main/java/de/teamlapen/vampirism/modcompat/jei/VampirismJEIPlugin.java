package de.teamlapen.vampirism.modcompat.jei;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.client.gui.AlchemicalCauldronScreen;
import de.teamlapen.vampirism.client.gui.PotionTableScreen;
import de.teamlapen.vampirism.client.gui.WeaponTableScreen;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.core.ModRecipes;
import de.teamlapen.vampirism.inventory.container.AlchemicalCauldronContainer;
import de.teamlapen.vampirism.inventory.container.WeaponTableContainer;
import de.teamlapen.vampirism.player.tasks.TaskUtil;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaRecipeCategoryUid;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.vanilla.IVanillaRecipeFactory;
import mezz.jei.api.registration.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTier;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Plugin for Just Enough Items
 */
@JeiPlugin
//Currently core mod is required for this to not crash the game Forge https://github.com/MinecraftForge/MinecraftForge/pull/6254
public class VampirismJEIPlugin implements IModPlugin {
    static final ResourceLocation WEAPON_TABLE_RECIPE_ID = new ResourceLocation("vampirism", "hunter_weapon");
    static final ResourceLocation ALCHEMICAL_CAULDRON_RECIPE_UID = new ResourceLocation("vampirism", "alchemical_cauldron");
    static final ResourceLocation TASK_RECIPE_UID = new ResourceLocation("vampirism", "task");
    static final ResourceLocation POTION_RECIPE_UID = new ResourceLocation("vampirism", "potion");
    private static final ResourceLocation ID = new ResourceLocation(REFERENCE.MODID, "plugin");

    @Nonnull
    @Override
    public ResourceLocation getPluginUid() {
        return ID;
    }


    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IGuiHelper helper = registration.getJeiHelpers().getGuiHelper();
        registration.addRecipeCategories(new AlchemicalCauldronRecipeCategory(helper), new WeaponTableRecipeCategory(helper), new TaskRecipeCategory(helper), new PotionTableRecipeCategory(helper));
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(AlchemicalCauldronScreen.class, 80, 34, 20, 15, ALCHEMICAL_CAULDRON_RECIPE_UID);
        registration.addRecipeClickArea(WeaponTableScreen.class, 114, 46, 20, 15, WEAPON_TABLE_RECIPE_ID);
        registration.addRecipeClickArea(PotionTableScreen.class, 145, 17,9,28, POTION_RECIPE_UID);
    }

    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
        registration.addRecipeTransferHandler(AlchemicalCauldronContainer.class, ALCHEMICAL_CAULDRON_RECIPE_UID, 0, 2, 4, 36);
        registration.addRecipeTransferHandler(AlchemicalCauldronContainer.class, VanillaRecipeCategoryUid.FUEL, 3, 1, 4, 36);
        registration.addRecipeTransferHandler(WeaponTableContainer.class, WEAPON_TABLE_RECIPE_ID, 1, 16, 17, 36);
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        ClientWorld world = Minecraft.getInstance().level;
        RecipeManager recipeManager = world.getRecipeManager();
        registration.addRecipes(recipeManager.byType(ModRecipes.ALCHEMICAL_CAULDRON_TYPE).values(), ALCHEMICAL_CAULDRON_RECIPE_UID);
        registration.addRecipes(recipeManager.byType(ModRecipes.WEAPONTABLE_CRAFTING_TYPE).values(), WEAPON_TABLE_RECIPE_ID);
        registration.addRecipes(TaskUtil.getItemRewardTasks(), TASK_RECIPE_UID);
        registration.addRecipes(VampirismAPI.extendedBrewingRecipeRegistry().getPotionMixes().stream().map(JEIPotionMix::createFromMix).flatMap(Collection::stream).collect(Collectors.toList()), POTION_RECIPE_UID);
        registration.addRecipes(getRepairRecipes(registration.getVanillaRecipeFactory()), VanillaRecipeCategoryUid.ANVIL);
    }

    @Override
    public void registerVanillaCategoryExtensions(IVanillaCategoryExtensionRegistration registration) {

    }

    private Collection<Object> getRepairRecipes(IVanillaRecipeFactory factory) {
        List<Object> recipes = new ArrayList<>();
        Map<Ingredient, List<Item>> items = Maps.newHashMap();
        Ingredient ironIngredient = ItemTier.IRON.getRepairIngredient();
        items.put(ironIngredient, Lists.newArrayList(ModItems.hunter_axe_normal, ModItems.hunter_axe_enhanced, ModItems.hunter_axe_ultimate, ModItems.basic_tech_crossbow, ModItems.enhanced_tech_crossbow, ModItems.hunter_coat_chest_normal, ModItems.hunter_coat_chest_enhanced, ModItems.hunter_coat_chest_ultimate, ModItems.hunter_coat_head_normal, ModItems.hunter_coat_head_enhanced, ModItems.hunter_coat_head_ultimate, ModItems.hunter_coat_legs_normal, ModItems.hunter_coat_legs_enhanced, ModItems.hunter_coat_legs_ultimate, ModItems.hunter_coat_feet_normal, ModItems.hunter_coat_feet_enhanced, ModItems.hunter_coat_feet_ultimate));
        Ingredient stringIngredient = Ingredient.of(Tags.Items.STRING);
        items.put(stringIngredient, Lists.newArrayList(ModItems.basic_crossbow, ModItems.basic_double_crossbow, ModItems.enhanced_crossbow, ModItems.enhanced_double_crossbow));
        Ingredient obsidianIngredient = Ingredient.of(Tags.Items.OBSIDIAN);
        items.put(obsidianIngredient, Lists.newArrayList(ModItems.obsidian_armor_chest_normal, ModItems.obsidian_armor_chest_enhanced, ModItems.obsidian_armor_chest_ultimate, ModItems.obsidian_armor_head_normal, ModItems.obsidian_armor_head_enhanced, ModItems.obsidian_armor_head_ultimate, ModItems.obsidian_armor_legs_normal, ModItems.obsidian_armor_legs_enhanced, ModItems.obsidian_armor_legs_ultimate, ModItems.obsidian_armor_feet_normal, ModItems.obsidian_armor_feet_enhanced, ModItems.obsidian_armor_feet_ultimate));
        Ingredient leather = Ingredient.of(Tags.Items.LEATHER);
        items.put(leather, Lists.newArrayList(ModItems.armor_of_swiftness_chest_normal, ModItems.armor_of_swiftness_chest_enhanced, ModItems.armor_of_swiftness_chest_ultimate, ModItems.armor_of_swiftness_head_normal, ModItems.armor_of_swiftness_head_enhanced, ModItems.armor_of_swiftness_head_ultimate, ModItems.armor_of_swiftness_legs_normal, ModItems.armor_of_swiftness_legs_enhanced, ModItems.armor_of_swiftness_legs_ultimate, ModItems.armor_of_swiftness_feet_normal, ModItems.armor_of_swiftness_feet_enhanced, ModItems.armor_of_swiftness_feet_ultimate));
        Ingredient bloodIngot = Ingredient.of(ModItems.blood_infused_iron_ingot);
        items.put(bloodIngot, Lists.newArrayList(ModItems.heart_seeker_normal, ModItems.heart_striker_normal));
        Ingredient enhancedBloodIngot = Ingredient.of(ModItems.blood_infused_enhanced_iron_ingot);
        items.put(enhancedBloodIngot, Lists.newArrayList(ModItems.heart_seeker_enhanced, ModItems.heart_seeker_ultimate, ModItems.heart_striker_enhanced, ModItems.heart_striker_ultimate));

        for (Map.Entry<Ingredient, List<Item>> entry : items.entrySet()) {

            List<ItemStack> repairMaterials = Lists.newArrayList(
                    entry.getKey().getItems()
            );

            for (Item ingredientItem : entry.getValue()) {
                ItemStack ingredient = new ItemStack(ingredientItem);
                ItemStack damaged1 = ingredient.copy();
                damaged1.setDamageValue(damaged1.getMaxDamage());
                ItemStack damaged2 = ingredient.copy();
                damaged2.setDamageValue(damaged2.getMaxDamage() * 3 / 4);
                ItemStack damaged3 = ingredient.copy();
                damaged3.setDamageValue(damaged3.getMaxDamage() * 2 / 4);

                if (!repairMaterials.isEmpty()) {
                    Object repairWithMaterial = factory.createAnvilRecipe(damaged1, repairMaterials, Collections.singletonList(damaged2));
                    recipes.add(repairWithMaterial);
                }
                Object repairWithSame = factory.createAnvilRecipe(damaged2, Collections.singletonList(damaged2), Collections.singletonList(damaged3));
                recipes.add(repairWithSame);
            }
        }
        return recipes;
    }
}

package de.teamlapen.vampirism.modcompat.jei;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.player.task.Task;
import de.teamlapen.vampirism.api.items.IWeaponTableRecipe;
import de.teamlapen.vampirism.client.gui.AlchemicalCauldronScreen;
import de.teamlapen.vampirism.client.gui.WeaponTableScreen;
import de.teamlapen.vampirism.core.ModContainer;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.core.ModRecipes;
import de.teamlapen.vampirism.inventory.container.AlchemicalCauldronContainer;
import de.teamlapen.vampirism.inventory.container.WeaponTableContainer;
import de.teamlapen.vampirism.inventory.recipes.AlchemicalCauldronRecipe;
import de.teamlapen.vampirism.mixin.RecipeManagerAccessor;
import de.teamlapen.vampirism.player.tasks.TaskUtil;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.vanilla.IJeiAnvilRecipe;
import mezz.jei.api.recipe.vanilla.IVanillaRecipeFactory;
import mezz.jei.api.registration.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraftforge.common.Tags;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Plugin for Just Enough Items
 */
@JeiPlugin
//Currently, core mod is required for this to not crash the game Forge https://github.com/MinecraftForge/MinecraftForge/pull/6254
public class VampirismJEIPlugin implements IModPlugin {
    public static final RecipeType<IWeaponTableRecipe> WEAPON_TABLE = RecipeType.create("vampirism", "hunter_weapon", IWeaponTableRecipe.class);
    public static final RecipeType<AlchemicalCauldronRecipe> ALCHEMICAL_CAULDRON = RecipeType.create("vampirism", "alchemical_cauldron", AlchemicalCauldronRecipe.class);
    public static final RecipeType<Task> TASK = RecipeType.create("vampirism", "task", Task.class);
    public static final RecipeType<JEIPotionMix> POTION = RecipeType.create("vampirism", "potion", JEIPotionMix.class);
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
        registration.addRecipeClickArea(AlchemicalCauldronScreen.class, 80, 34, 20, 15, ALCHEMICAL_CAULDRON);
        registration.addRecipeClickArea(WeaponTableScreen.class, 114, 46, 20, 15, WEAPON_TABLE);
    }

    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
        registration.addRecipeTransferHandler(AlchemicalCauldronContainer.class, ModContainer.ALCHEMICAL_CAULDRON.get(), ALCHEMICAL_CAULDRON, 0, 2, 4, 36);
        registration.addRecipeTransferHandler(AlchemicalCauldronContainer.class, ModContainer.ALCHEMICAL_CAULDRON.get(), RecipeTypes.FUELING, 3, 1, 4, 36);
        registration.addRecipeTransferHandler(WeaponTableContainer.class, ModContainer.WEAPON_TABLE.get(), WEAPON_TABLE, 1, 16, 17, 36);
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        ClientLevel world = Minecraft.getInstance().level;
        RecipeManager recipeManager = world.getRecipeManager();
        registration.addRecipes(ALCHEMICAL_CAULDRON, ((RecipeManagerAccessor)recipeManager).getByType(ModRecipes.ALCHEMICAL_CAULDRON_TYPE.get()).values().stream().toList());
        registration.addRecipes(WEAPON_TABLE, ((RecipeManagerAccessor) recipeManager).getByType(ModRecipes.WEAPONTABLE_CRAFTING_TYPE.get()).values().stream().toList());
        registration.addRecipes(TASK, TaskUtil.getItemRewardTasks());
        registration.addRecipes(POTION, VampirismAPI.extendedBrewingRecipeRegistry().getPotionMixes().stream().map(JEIPotionMix::createFromMix).flatMap(Collection::stream).collect(Collectors.toList()));
        registration.addRecipes(RecipeTypes.ANVIL, getRepairRecipes(registration.getVanillaRecipeFactory()));
    }

    @Override
    public void registerVanillaCategoryExtensions(@Nonnull IVanillaCategoryExtensionRegistration registration) {

    }

    private List<IJeiAnvilRecipe> getRepairRecipes(IVanillaRecipeFactory factory) {
        List<IJeiAnvilRecipe> recipes = new ArrayList<>();
        Map<Ingredient, List<Item>> items = Maps.newHashMap();
        Ingredient ironIngredient = Tiers.IRON.getRepairIngredient();
        items.put(ironIngredient, Lists.newArrayList(ModItems.HUNTER_AXE_NORMAL.get(), ModItems.HUNTER_AXE_ENHANCED.get(), ModItems.HUNTER_AXE_ULTIMATE.get(), ModItems.BASIC_TECH_CROSSBOW.get(), ModItems.ENHANCED_TECH_CROSSBOW.get(), ModItems.HUNTER_COAT_CHEST_NORMAL.get(), ModItems.HUNTER_COAT_CHEST_ENHANCED.get(), ModItems.HUNTER_COAT_CHEST_ULTIMATE.get(), ModItems.HUNTER_COAT_HEAD_NORMAL.get(), ModItems.HUNTER_COAT_HEAD_ENHANCED.get(), ModItems.HUNTER_COAT_HEAD_ULTIMATE.get(), ModItems.HUNTER_COAT_LEGS_NORMAL.get(), ModItems.HUNTER_COAT_LEGS_ENHANCED.get(), ModItems.HUNTER_COAT_LEGS_ULTIMATE.get(), ModItems.HUNTER_COAT_FEET_NORMAL.get(), ModItems.HUNTER_COAT_FEET_ENHANCED.get(), ModItems.HUNTER_COAT_FEET_ULTIMATE.get()));
        Ingredient stringIngredient = Ingredient.of(Tags.Items.STRING);
        items.put(stringIngredient, Lists.newArrayList(ModItems.BASIC_CROSSBOW.get(), ModItems.BASIC_DOUBLE_CROSSBOW.get(), ModItems.ENHANCED_CROSSBOW.get(), ModItems.ENHANCED_DOUBLE_CROSSBOW.get()));
        Ingredient leather = Ingredient.of(Tags.Items.LEATHER);
        items.put(leather, Lists.newArrayList(ModItems.ARMOR_OF_SWIFTNESS_CHEST_NORMAL.get(), ModItems.ARMOR_OF_SWIFTNESS_CHEST_ENHANCED.get(), ModItems.ARMOR_OF_SWIFTNESS_CHEST_ULTIMATE.get(), ModItems.ARMOR_OF_SWIFTNESS_HEAD_NORMAL.get(), ModItems.ARMOR_OF_SWIFTNESS_HEAD_ENHANCED.get(), ModItems.ARMOR_OF_SWIFTNESS_HEAD_ULTIMATE.get(), ModItems.ARMOR_OF_SWIFTNESS_LEGS_NORMAL.get(), ModItems.ARMOR_OF_SWIFTNESS_LEGS_ENHANCED.get(), ModItems.ARMOR_OF_SWIFTNESS_LEGS_ULTIMATE.get(), ModItems.ARMOR_OF_SWIFTNESS_FEET_NORMAL.get(), ModItems.ARMOR_OF_SWIFTNESS_FEET_ENHANCED.get(), ModItems.ARMOR_OF_SWIFTNESS_FEET_ULTIMATE.get()));
        Ingredient bloodIngot = Ingredient.of(ModItems.BLOOD_INFUSED_IRON_INGOT.get());
        items.put(bloodIngot, Lists.newArrayList(ModItems.HEART_SEEKER_NORMAL.get(), ModItems.HEART_STRIKER_NORMAL.get()));
        Ingredient enhancedBloodIngot = Ingredient.of(ModItems.BLOOD_INFUSED_ENHANCED_IRON_INGOT.get());
        items.put(enhancedBloodIngot, Lists.newArrayList(ModItems.HEART_SEEKER_ENHANCED.get(), ModItems.HEART_SEEKER_ULTIMATE.get(), ModItems.HEART_STRIKER_ENHANCED.get(), ModItems.HEART_STRIKER_ULTIMATE.get()));

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
                    IJeiAnvilRecipe repairWithMaterial = factory.createAnvilRecipe(damaged1, repairMaterials, Collections.singletonList(damaged2));
                    recipes.add(repairWithMaterial);
                }
                IJeiAnvilRecipe repairWithSame = factory.createAnvilRecipe(damaged2, Collections.singletonList(damaged2), Collections.singletonList(damaged3));
                recipes.add(repairWithSame);
            }
        }
        return recipes;
    }
}

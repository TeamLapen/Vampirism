package de.teamlapen.vampirism.modcompat.jei;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.items.oil.IApplicableOil;
import de.teamlapen.vampirism.client.gui.AlchemicalCauldronScreen;
import de.teamlapen.vampirism.client.gui.AlchemyTableScreen;
import de.teamlapen.vampirism.client.gui.PotionTableScreen;
import de.teamlapen.vampirism.client.gui.WeaponTableScreen;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.core.ModRecipes;
import de.teamlapen.vampirism.core.ModRegistries;
import de.teamlapen.vampirism.inventory.container.AlchemicalCauldronContainer;
import de.teamlapen.vampirism.inventory.container.WeaponTableContainer;
import de.teamlapen.vampirism.items.BlessableItem;
import de.teamlapen.vampirism.player.tasks.TaskUtil;
import de.teamlapen.vampirism.util.OilUtils;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaRecipeCategoryUid;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.subtypes.IIngredientSubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.api.recipe.vanilla.IVanillaRecipeFactory;
import mezz.jei.api.registration.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTier;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.item.crafting.ShapelessRecipe;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.ForgeRegistries;

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
    static final ResourceLocation BLESSING_RECIPE_UID = new ResourceLocation("vampirism", "blessing");
    static final ResourceLocation POTION_RECIPE_UID = new ResourceLocation("vampirism", "potion");
    static final ResourceLocation ALCHEMY_TABLE_UID = new ResourceLocation("vampirism", "alchemy_table");
    private static final ResourceLocation ID = new ResourceLocation(REFERENCE.MODID, "plugin");

    @Nonnull
    @Override
    public ResourceLocation getPluginUid() {
        return ID;
    }


    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IGuiHelper helper = registration.getJeiHelpers().getGuiHelper();
        registration.addRecipeCategories(new AlchemicalCauldronRecipeCategory(helper), new WeaponTableRecipeCategory(helper), new TaskRecipeCategory(helper), new PotionTableRecipeCategory(helper), new AlchemyTableRecipeCategory(helper), new BlessingRecipeCategory(helper));
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(AlchemicalCauldronScreen.class, 80, 34, 20, 15, ALCHEMICAL_CAULDRON_RECIPE_UID);
        registration.addRecipeClickArea(WeaponTableScreen.class, 114, 46, 20, 15, WEAPON_TABLE_RECIPE_ID);
        registration.addRecipeClickArea(PotionTableScreen.class, 145, 17,9,28, POTION_RECIPE_UID);
        registration.addRecipeClickArea(AlchemyTableScreen.class, 73,57,28, 8, ALCHEMY_TABLE_UID);
        registration.addRecipeClickArea(AlchemyTableScreen.class, 104,36,32, 32, ALCHEMY_TABLE_UID);
    }

    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
        registration.addRecipeTransferHandler(AlchemicalCauldronContainer.class, ALCHEMICAL_CAULDRON_RECIPE_UID, 0, 2, 4, 36);
        registration.addRecipeTransferHandler(AlchemicalCauldronContainer.class, VanillaRecipeCategoryUid.FUEL, 3, 1, 4, 36);
        registration.addRecipeTransferHandler(WeaponTableContainer.class, WEAPON_TABLE_RECIPE_ID, 1, 16, 17, 36);
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration) {
        registration.registerSubtypeInterpreter(ModItems.OIL_BOTTLE.get(), OilNBT.INSTANCE);
    }

    private static class OilNBT implements IIngredientSubtypeInterpreter<ItemStack> {
        public static final OilNBT INSTANCE = new OilNBT();

        private OilNBT() {
        }

        @Override
        public String apply(ItemStack itemStack, UidContext context) {
            CompoundNBT nbtTagCompound = itemStack.getTag();
            if (nbtTagCompound == null || nbtTagCompound.isEmpty()) {
                return IIngredientSubtypeInterpreter.NONE;
            }
            return OilUtils.getOil(itemStack).getRegistryName().toString();
        }
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
        registration.addRecipes(recipeManager.byType(ModRecipes.ALCHEMICAL_TABLE_TYPE).values(), ALCHEMY_TABLE_UID);
        registration.addRecipes(getApplicableOilRecipes(), VanillaRecipeCategoryUid.CRAFTING);
        registration.addRecipes(BlessableItem.getBlessableRecipes(), BLESSING_RECIPE_UID);
    }

    @Override
    public void registerVanillaCategoryExtensions(IVanillaCategoryExtensionRegistration registration) {

    }

    private Collection<Object> getRepairRecipes(IVanillaRecipeFactory factory) {
        List<Object> recipes = new ArrayList<>();
        Map<Ingredient, List<Item>> items = Maps.newHashMap();
        Ingredient ironIngredient = ItemTier.IRON.getRepairIngredient();
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
                    Object repairWithMaterial = factory.createAnvilRecipe(damaged1, repairMaterials, Collections.singletonList(damaged2));
                    recipes.add(repairWithMaterial);
                }
                Object repairWithSame = factory.createAnvilRecipe(damaged2, Collections.singletonList(damaged2), Collections.singletonList(damaged3));
                recipes.add(repairWithSame);
            }
        }
        return recipes;
    }

    private List<ShapelessRecipe> getApplicableOilRecipes() {
        return ModRegistries.OILS.getValues().stream()
                .filter(IApplicableOil.class::isInstance)
                .map(IApplicableOil.class::cast)
                .flatMap(oil -> ForgeRegistries.ITEMS.getValues().stream()
                        .map(Item::getDefaultInstance)
                        .filter(oil::canBeApplied)
                        .map(stack -> new ShapelessRecipe(new ResourceLocation(REFERENCE.MODID, (oil.getRegistryName().toString() + stack.getItem().getRegistryName().toString()).replace(':', '_')), "", OilUtils.setAppliedOil(stack.copy(), oil), NonNullList.of(Ingredient.EMPTY, Ingredient.of(stack), Ingredient.of(OilUtils.createOilItem(oil))))
                        )).collect(Collectors.toList());
    }
}

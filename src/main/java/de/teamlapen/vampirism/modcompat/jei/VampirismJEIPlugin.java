package de.teamlapen.vampirism.modcompat.jei;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.player.task.Task;
import de.teamlapen.vampirism.api.items.IFactionExclusiveItem;
import de.teamlapen.vampirism.api.items.IWeaponTableRecipe;
import de.teamlapen.vampirism.api.items.oil.IApplicableOil;
import de.teamlapen.vampirism.client.gui.screens.AlchemicalCauldronScreen;
import de.teamlapen.vampirism.client.gui.screens.AlchemyTableScreen;
import de.teamlapen.vampirism.client.gui.screens.PotionTableScreen;
import de.teamlapen.vampirism.client.gui.screens.WeaponTableScreen;
import de.teamlapen.vampirism.core.*;
import de.teamlapen.vampirism.data.provider.SundamageProvider;
import de.teamlapen.vampirism.entity.player.tasks.TaskUtil;
import de.teamlapen.vampirism.inventory.AlchemicalCauldronMenu;
import de.teamlapen.vampirism.inventory.WeaponTableMenu;
import de.teamlapen.vampirism.items.BlessableItem;
import de.teamlapen.vampirism.mixin.RecipeManagerAccessor;
import de.teamlapen.vampirism.recipes.AlchemicalCauldronRecipe;
import de.teamlapen.vampirism.recipes.AlchemyTableRecipe;
import de.teamlapen.vampirism.util.OilUtils;
import de.teamlapen.vampirism.util.RegUtil;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.subtypes.IIngredientSubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.vanilla.IJeiAnvilRecipe;
import mezz.jei.api.recipe.vanilla.IVanillaRecipeFactory;
import mezz.jei.api.registration.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.*;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Plugin for Just Enough Items
 */
@JeiPlugin
//Currently, core mod is required for this to not crash the game Forge https://github.com/MinecraftForge/MinecraftForge/pull/6254
public class VampirismJEIPlugin implements IModPlugin {
    public static final RecipeType<IWeaponTableRecipe> WEAPON_TABLE = RecipeType.create("vampirism", "hunter_weapon", IWeaponTableRecipe.class);
    public static final RecipeType<AlchemicalCauldronRecipe> ALCHEMICAL_CAULDRON = RecipeType.create("vampirism", "alchemical_cauldron", AlchemicalCauldronRecipe.class);
    public static final RecipeType<Task> TASK = RecipeType.create("vampirism", "task", Task.class);
    public static final RecipeType<BlessableItem.Recipe> BLESSING = RecipeType.create("vampirism", "blessing", BlessableItem.Recipe.class);
    public static final RecipeType<JEIPotionMix> POTION = RecipeType.create("vampirism", "potion", JEIPotionMix.class);
    public static final RecipeType<AlchemyTableRecipe> ALCHEMY_TABLE = RecipeType.create("vampirism", "alchemy_table", AlchemyTableRecipe.class);
    private static final ResourceLocation ID = new ResourceLocation(REFERENCE.MODID, "plugin");

    @NotNull
    @Override
    public ResourceLocation getPluginUid() {
        return ID;
    }


    @Override
    public void registerCategories(@NotNull IRecipeCategoryRegistration registration) {
        IGuiHelper helper = registration.getJeiHelpers().getGuiHelper();
        registration.addRecipeCategories(new AlchemicalCauldronRecipeCategory(helper), new WeaponTableRecipeCategory(helper), new TaskRecipeCategory(helper), new PotionTableRecipeCategory(helper), new AlchemyTableRecipeCategory(helper), new BlessingRecipeCategory(helper));
    }

    @Override
    public void registerGuiHandlers(@NotNull IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(AlchemicalCauldronScreen.class, 80, 34, 20, 15, ALCHEMICAL_CAULDRON);
        registration.addRecipeClickArea(WeaponTableScreen.class, 114, 46, 20, 15, WEAPON_TABLE);
        registration.addRecipeClickArea(PotionTableScreen.class, 145, 17, 9, 28, POTION);
        registration.addRecipeClickArea(AlchemyTableScreen.class, 73, 57, 28, 8, ALCHEMY_TABLE);
        registration.addRecipeClickArea(AlchemyTableScreen.class, 104, 36, 32, 32, ALCHEMY_TABLE);
    }

    @Override
    public void registerRecipeTransferHandlers(@NotNull IRecipeTransferRegistration registration) {
        registration.addRecipeTransferHandler(AlchemicalCauldronMenu.class, ModContainer.ALCHEMICAL_CAULDRON.get(), ALCHEMICAL_CAULDRON, 0, 2, 4, 36);
        registration.addRecipeTransferHandler(AlchemicalCauldronMenu.class, ModContainer.ALCHEMICAL_CAULDRON.get(), RecipeTypes.FUELING, 3, 1, 4, 36);
        registration.addRecipeTransferHandler(WeaponTableMenu.class, ModContainer.WEAPON_TABLE.get(), WEAPON_TABLE, 1, 16, 17, 36);
    }

    @Override
    public void registerItemSubtypes(@NotNull ISubtypeRegistration registration) {
        registration.registerSubtypeInterpreter(ModItems.OIL_BOTTLE.get(), OilNBT.INSTANCE);
    }

    private static class OilNBT implements IIngredientSubtypeInterpreter<ItemStack> {
        public static final OilNBT INSTANCE = new OilNBT();

        private OilNBT() {
        }

        @Override
        public @NotNull String apply(@NotNull ItemStack itemStack, UidContext context) {
            CompoundTag nbtTagCompound = itemStack.getTag();
            if (nbtTagCompound == null || nbtTagCompound.isEmpty()) {
                return IIngredientSubtypeInterpreter.NONE;
            }
            return RegUtil.id(OilUtils.getOil(itemStack)).toString();
        }
    }

    @Override
    public void registerRecipes(@NotNull IRecipeRegistration registration) {
        ClientLevel world = Minecraft.getInstance().level;
        RecipeManager recipeManager = world.getRecipeManager();
        registration.addRecipes(ALCHEMICAL_CAULDRON, ((RecipeManagerAccessor) recipeManager).getByType(ModRecipes.ALCHEMICAL_CAULDRON_TYPE.get()).values().stream().toList());
        registration.addRecipes(WEAPON_TABLE, ((RecipeManagerAccessor) recipeManager).getByType(ModRecipes.WEAPONTABLE_CRAFTING_TYPE.get()).values().stream().toList());
        registration.addRecipes(TASK, TaskUtil.getItemRewardTasks(world.registryAccess()));
        registration.addRecipes(POTION, VampirismAPI.extendedBrewingRecipeRegistry().getPotionMixes().stream().map(JEIPotionMix::createFromMix).flatMap(Collection::stream).collect(Collectors.toList()));
        registration.addRecipes(RecipeTypes.ANVIL, getRepairRecipes(registration.getVanillaRecipeFactory()));
        registration.addRecipes(ALCHEMY_TABLE, recipeManager.byType(ModRecipes.ALCHEMICAL_TABLE_TYPE.get()).values().stream().toList());
        registration.addRecipes(RecipeTypes.CRAFTING, getApplicableOilRecipes());
        registration.addRecipes(BLESSING, BlessableItem.getBlessableRecipes());
        registration.addRecipes(RecipeTypes.CRAFTING, getCleanOilRecipes(world.registryAccess()));
    }

    @Override
    public void registerVanillaCategoryExtensions(@NotNull IVanillaCategoryExtensionRegistration registration) {

    }

    private @NotNull <T extends Item> List<IJeiAnvilRecipe> getRepairRecipes(@NotNull IVanillaRecipeFactory factory) {
        List<IJeiAnvilRecipe> recipes = new ArrayList<>();
        addRepairItem(factory, recipes::add, axe -> axe.getTier().getRepairIngredient(), ModItems.HUNTER_AXE_NORMAL.get(), ModItems.HUNTER_AXE_ENHANCED.get(), ModItems.HUNTER_AXE_ULTIMATE.get(), ModItems.HEART_SEEKER_ENHANCED.get(), ModItems.HEART_SEEKER_ULTIMATE.get(), ModItems.HEART_STRIKER_ENHANCED.get(), ModItems.HEART_STRIKER_ULTIMATE.get(), ModItems.HEART_SEEKER_NORMAL.get(), ModItems.HEART_STRIKER_NORMAL.get());
        addRepairItem(factory, recipes::add, armor -> armor.getMaterial().getRepairIngredient(), ModItems.HUNTER_COAT_CHEST_NORMAL.get(), ModItems.HUNTER_COAT_CHEST_ENHANCED.get(), ModItems.HUNTER_COAT_CHEST_ULTIMATE.get(), ModItems.HUNTER_COAT_HEAD_NORMAL.get(), ModItems.HUNTER_COAT_HEAD_ENHANCED.get(), ModItems.HUNTER_COAT_HEAD_ULTIMATE.get(), ModItems.HUNTER_COAT_LEGS_NORMAL.get(), ModItems.HUNTER_COAT_LEGS_ENHANCED.get(), ModItems.HUNTER_COAT_LEGS_ULTIMATE.get(), ModItems.HUNTER_COAT_FEET_NORMAL.get(), ModItems.HUNTER_COAT_FEET_ENHANCED.get(), ModItems.HUNTER_COAT_FEET_ULTIMATE.get(), ModItems.ARMOR_OF_SWIFTNESS_CHEST_NORMAL.get(), ModItems.ARMOR_OF_SWIFTNESS_CHEST_ENHANCED.get(), ModItems.ARMOR_OF_SWIFTNESS_CHEST_ULTIMATE.get(), ModItems.ARMOR_OF_SWIFTNESS_HEAD_NORMAL.get(), ModItems.ARMOR_OF_SWIFTNESS_HEAD_ENHANCED.get(), ModItems.ARMOR_OF_SWIFTNESS_HEAD_ULTIMATE.get(), ModItems.ARMOR_OF_SWIFTNESS_LEGS_NORMAL.get(), ModItems.ARMOR_OF_SWIFTNESS_LEGS_ENHANCED.get(), ModItems.ARMOR_OF_SWIFTNESS_LEGS_ULTIMATE.get(), ModItems.ARMOR_OF_SWIFTNESS_FEET_NORMAL.get(), ModItems.ARMOR_OF_SWIFTNESS_FEET_ENHANCED.get(), ModItems.ARMOR_OF_SWIFTNESS_FEET_ULTIMATE.get(), ModItems.VAMPIRE_CLOTHING_CROWN.get(), ModItems.VAMPIRE_CLOTHING_HAT.get(), ModItems.VAMPIRE_CLOTHING_LEGS.get(), ModItems.VAMPIRE_CLOTHING_BOOTS.get(), ModItems.VAMPIRE_CLOAK_RED_BLACK.get(), ModItems.VAMPIRE_CLOAK_BLACK_RED.get(), ModItems.VAMPIRE_CLOAK_BLACK_WHITE.get(), ModItems.VAMPIRE_CLOAK_WHITE_BLACK.get(), ModItems.VAMPIRE_CLOAK_BLACK_BLUE.get());
        addRepairItem(factory, recipes::add, crossbow -> Ingredient.of(Items.STRING), ModItems.BASIC_CROSSBOW.get(), ModItems.BASIC_DOUBLE_CROSSBOW.get(), ModItems.ENHANCED_CROSSBOW.get(), ModItems.ENHANCED_DOUBLE_CROSSBOW.get());
        addRepairItem(factory, recipes::add, crossbow -> Ingredient.of(Items.IRON_INGOT), ModItems.BASIC_TECH_CROSSBOW.get(), ModItems.ENHANCED_TECH_CROSSBOW.get());
        return recipes;
    }

    private <T extends Item> void addRepairItem(@NotNull IVanillaRecipeFactory factory, Consumer<IJeiAnvilRecipe> consumer, Function<T, Ingredient> ingredientFunction, T... items) {
        for (T item : items) {
            ItemStack damageThreeQuarters = item.getDefaultInstance();
            damageThreeQuarters.setDamageValue(damageThreeQuarters.getMaxDamage() * 3 / 4);
            ItemStack damageHalf = item.getDefaultInstance();
            damageHalf.setDamageValue(damageHalf.getMaxDamage() / 2);

            IJeiAnvilRecipe repairWithSame = factory.createAnvilRecipe(List.of(damageThreeQuarters), List.of(damageThreeQuarters), List.of(damageHalf));
            consumer.accept(repairWithSame);
            Optional.ofNullable(ingredientFunction.apply(item)).map(s -> s.getItems()).filter(s -> s.length > 0).map(s -> List.of(s)).ifPresent(repairMaterials -> {
                ItemStack damagedFully = item.getDefaultInstance();
                damagedFully.setDamageValue(damagedFully.getMaxDamage());
                IJeiAnvilRecipe repairWithMaterial = factory.createAnvilRecipe(List.of(damagedFully), repairMaterials, List.of(damageThreeQuarters));
                consumer.accept(repairWithMaterial);
            });
        }
    }

    private @NotNull List<CraftingRecipe> getApplicableOilRecipes() {
        return RegUtil.values(ModRegistries.OILS).stream()
                .filter(IApplicableOil.class::isInstance)
                .map(IApplicableOil.class::cast)
                .flatMap(oil -> ForgeRegistries.ITEMS.getValues().stream()
                        .map(Item::getDefaultInstance)
                        .filter(item -> (!(item.getItem() instanceof IFactionExclusiveItem) || ((IFactionExclusiveItem) item.getItem()).getExclusiveFaction(item) == VReference.HUNTER_FACTION))
                        .filter(oil::canBeApplied)
                        .map(stack -> new ShapelessRecipe(new ResourceLocation(REFERENCE.MODID, (RegUtil.id(oil).toString() + RegUtil.id(stack.getItem()).toString()).replace(':', '_')), "", CraftingBookCategory.EQUIPMENT, OilUtils.setAppliedOil(stack.copy(), oil), NonNullList.of(Ingredient.EMPTY, Ingredient.of(stack), Ingredient.of(OilUtils.createOilItem(oil)))))).collect(Collectors.toList());
    }

    private @NotNull List<CraftingRecipe> getCleanOilRecipes(RegistryAccess registryAccess) {
        return getApplicableOilRecipes().stream().map(recipe -> {
            ItemStack item = recipe.getResultItem(registryAccess);
            IApplicableOil oil = OilUtils.getAppliedOil(item).get();
            return new ShapelessRecipe(new ResourceLocation(REFERENCE.MODID, ("clean_" + RegUtil.id(oil).toString() + "_from_" + RegUtil.id(item.getItem()).toString()).replace(':', '_')), "", CraftingBookCategory.EQUIPMENT, OilUtils.removeAppliedOil(item.copy()), NonNullList.of(Ingredient.EMPTY, Ingredient.of(Items.PAPER), Ingredient.of(item)));
        }).collect(Collectors.toList());
    }
}

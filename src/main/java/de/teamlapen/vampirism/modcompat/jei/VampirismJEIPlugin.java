package de.teamlapen.vampirism.modcompat.jei;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.player.task.Task;
import de.teamlapen.vampirism.api.items.IFactionExclusiveItem;
import de.teamlapen.vampirism.api.items.IWeaponTableRecipe;
import de.teamlapen.vampirism.api.items.oil.IApplicableOil;
import de.teamlapen.vampirism.api.util.VResourceLocation;
import de.teamlapen.vampirism.client.gui.screens.*;
import de.teamlapen.vampirism.client.gui.screens.diffuser.FogDiffuserScreen;
import de.teamlapen.vampirism.client.gui.screens.diffuser.GarlicDiffuserScreen;
import de.teamlapen.vampirism.core.*;
import de.teamlapen.vampirism.core.tags.ModItemTags;
import de.teamlapen.vampirism.entity.player.tasks.TaskUtil;
import de.teamlapen.vampirism.inventory.AlchemicalCauldronMenu;
import de.teamlapen.vampirism.inventory.WeaponTableMenu;
import de.teamlapen.vampirism.items.BlessableItem;
import de.teamlapen.vampirism.items.component.AppliedOilContent;
import de.teamlapen.vampirism.items.component.BottleBlood;
import de.teamlapen.vampirism.items.component.OilContent;
import de.teamlapen.vampirism.modcompat.jei.categories.FogDiffuserRecipeCategory;
import de.teamlapen.vampirism.modcompat.jei.categories.GarlicDiffuserRecipeCategory;
import de.teamlapen.vampirism.modcompat.jei.recipes.BloodSieveRecipe;
import de.teamlapen.vampirism.modcompat.jei.recipes.FogDiffuserRecipe;
import de.teamlapen.vampirism.modcompat.jei.recipes.GarlicDiffuserRecipe;
import de.teamlapen.vampirism.modcompat.jei.recipes.GrinderRecipe;
import de.teamlapen.vampirism.modcompat.jei.recipes.maker.BloodSieveRecipeMaker;
import de.teamlapen.vampirism.modcompat.jei.recipes.maker.FogDiffuserRecipeMaker;
import de.teamlapen.vampirism.modcompat.jei.recipes.maker.GarlicDiffuserRecipeMaker;
import de.teamlapen.vampirism.modcompat.jei.recipes.maker.GrinderRecipeMaker;
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
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.crafting.*;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Plugin for Just Enough Items
 */
@JeiPlugin
//Currently, core mod is required for this to not crash the game Forge https://github.com/MinecraftForge/MinecraftForge/pull/6254
public class VampirismJEIPlugin implements IModPlugin {
    public static final RecipeType<RecipeHolder<IWeaponTableRecipe>> WEAPON_TABLE = createFromModded(ModRecipes.WEAPONTABLE_CRAFTING_TYPE);
    public static final RecipeType<RecipeHolder<AlchemicalCauldronRecipe>> ALCHEMICAL_CAULDRON = createFromModded(ModRecipes.ALCHEMICAL_CAULDRON_TYPE);
    public static final RecipeType<Task> TASK = RecipeType.create("vampirism", "task", Task.class);
    public static final RecipeType<BlessableItem.Recipe> BLESSING = RecipeType.create("vampirism", "blessing", BlessableItem.Recipe.class);
    public static final RecipeType<JEIPotionMix> POTION = RecipeType.create("vampirism", "potion", JEIPotionMix.class);
    public static final RecipeType<RecipeHolder<AlchemyTableRecipe>> ALCHEMY_TABLE = createFromModded(ModRecipes.ALCHEMICAL_TABLE_TYPE);
    public static final RecipeType<GarlicDiffuserRecipe> GARLIC_DIFFUSER = RecipeType.create(REFERENCE.MODID, "garlic_diffuser", GarlicDiffuserRecipe.class);
    public static final RecipeType<FogDiffuserRecipe> FOG_DIFFUSER = RecipeType.create(REFERENCE.MODID, "fog_diffuser", FogDiffuserRecipe.class);
    public static final RecipeType<GrinderRecipe> GRINDER_RECIPE = RecipeType.create(REFERENCE.MODID, "grinder", GrinderRecipe.class);
    public static final RecipeType<BloodSieveRecipe> BLOOD_SIEVE_CONVERSION = RecipeType.create(REFERENCE.MODID, "blood_sieve", BloodSieveRecipe.class);

    private static final ResourceLocation ID = VResourceLocation.mod("plugin");

    @NotNull
    @Override
    public ResourceLocation getPluginUid() {
        return ID;
    }

    public static <R extends Recipe<?>> RecipeType<RecipeHolder<R>> createFromModded(DeferredHolder<net.minecraft.world.item.crafting.RecipeType<?>, net.minecraft.world.item.crafting.RecipeType<R>> vanillaRecipeType) {
        @SuppressWarnings({"unchecked", "RedundantCast"})
        Class<? extends RecipeHolder<R>> holderClass = (Class<? extends RecipeHolder<R>>) (Object) RecipeHolder.class;
        return new RecipeType<>(vanillaRecipeType.getId(), holderClass);
    }


    @Override
    public void registerCategories(@NotNull IRecipeCategoryRegistration registration) {
        IGuiHelper helper = registration.getJeiHelpers().getGuiHelper();
        registration.addRecipeCategories(
                new AlchemicalCauldronRecipeCategory(helper),
                new WeaponTableRecipeCategory(helper),
                new TaskRecipeCategory(helper),
                new PotionTableRecipeCategory(helper),
                new AlchemyTableRecipeCategory(helper),
                new BlessingRecipeCategory(helper),
                new GarlicDiffuserRecipeCategory(helper),
                new FogDiffuserRecipeCategory(helper),
                new GrinderRecipeCategory(helper),
                new BloodSieveRecipeCategory(helper));
    }

    @Override
    public void registerGuiHandlers(@NotNull IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(AlchemicalCauldronScreen.class, 80, 34, 20, 15, ALCHEMICAL_CAULDRON);
        registration.addRecipeClickArea(WeaponTableScreen.class, 114, 46, 20, 15, WEAPON_TABLE);
        registration.addRecipeClickArea(PotionTableScreen.class, 145, 17, 9, 28, POTION);
        registration.addRecipeClickArea(AlchemyTableScreen.class, 73, 57, 28, 8, ALCHEMY_TABLE);
        registration.addRecipeClickArea(AlchemyTableScreen.class, 104, 36, 32, 32, ALCHEMY_TABLE);
        registration.addRecipeClickArea(GarlicDiffuserScreen.class, 45, 55, 14, 14, GARLIC_DIFFUSER);
        registration.addRecipeClickArea(FogDiffuserScreen.class, 45, 55, 14, 14, FOG_DIFFUSER);
        registration.addRecipeClickArea(BloodGrinderScreen.class, 80, 55, 16, 16, GRINDER_RECIPE);
    }

    @Override
    public void registerRecipeTransferHandlers(@NotNull IRecipeTransferRegistration registration) {
        registration.addRecipeTransferHandler(AlchemicalCauldronMenu.class, ModMenus.ALCHEMICAL_CAULDRON.get(), ALCHEMICAL_CAULDRON, 0, 2, 4, 36);
        registration.addRecipeTransferHandler(AlchemicalCauldronMenu.class, ModMenus.ALCHEMICAL_CAULDRON.get(), RecipeTypes.FUELING, 3, 1, 4, 36);
        registration.addRecipeTransferHandler(WeaponTableMenu.class, ModMenus.WEAPON_TABLE.get(), WEAPON_TABLE, 1, 16, 17, 36);
    }

    @Override
    public void registerItemSubtypes(@NotNull ISubtypeRegistration registration) {
        registration.registerSubtypeInterpreter(ModItems.OIL_BOTTLE.get(), OilNBT.INSTANCE);
        registration.registerSubtypeInterpreter(ModItems.BLOOD_BOTTLE.get(), BloodNBT.INSTANCE);
    }

    private static class OilNBT implements IIngredientSubtypeInterpreter<ItemStack> {
        public static final OilNBT INSTANCE = new OilNBT();

        private OilNBT() {
        }

        @Override
        public @NotNull String apply(@NotNull ItemStack itemStack, @NotNull UidContext context) {
            return Optional.ofNullable(itemStack.get(ModDataComponents.OIL)).map(OilContent::oil).flatMap(Holder::unwrapKey).map(ResourceKey::location).map(ResourceLocation::toString).orElse(IIngredientSubtypeInterpreter.NONE);
        }
    }

    private static class BloodNBT implements IIngredientSubtypeInterpreter<ItemStack> {
        public static final BloodNBT INSTANCE = new BloodNBT();

        private BloodNBT() {
        }

        @Override
        public @NotNull String apply(@NotNull ItemStack itemStack, @NotNull UidContext context) {
            return Optional.ofNullable(itemStack.get(ModDataComponents.BOTTLE_BLOOD)).map(BottleBlood::blood).map(Object::toString).orElse(IIngredientSubtypeInterpreter.NONE);
        }
    }

    @Override
    public void registerRecipes(@NotNull IRecipeRegistration registration) {
        ClientLevel world = Minecraft.getInstance().level;
        RecipeManager recipeManager = world.getRecipeManager();
        registration.addRecipes(ALCHEMICAL_CAULDRON, recipeManager.getAllRecipesFor(ModRecipes.ALCHEMICAL_CAULDRON_TYPE.get()));
        registration.addRecipes(WEAPON_TABLE, recipeManager.getAllRecipesFor(ModRecipes.WEAPONTABLE_CRAFTING_TYPE.get()));
        registration.addRecipes(TASK, TaskUtil.getItemRewardTasks(world.registryAccess()));
        registration.addRecipes(POTION, VampirismAPI.extendedBrewingRecipeRegistry().getPotionMixes().stream().map(JEIPotionMix::createFromMix).flatMap(Collection::stream).collect(Collectors.toList()));
        registration.addRecipes(RecipeTypes.ANVIL, getRepairRecipes(registration.getVanillaRecipeFactory()));
        registration.addRecipes(ALCHEMY_TABLE, recipeManager.getAllRecipesFor(ModRecipes.ALCHEMICAL_TABLE_TYPE.get()));
        registration.addRecipes(RecipeTypes.CRAFTING, getApplicableOilRecipes());
        registration.addRecipes(BLESSING, BlessableItem.getBlessableRecipes());
        registration.addRecipes(RecipeTypes.CRAFTING, getCleanOilRecipes(world.registryAccess()));
        registration.addRecipes(GARLIC_DIFFUSER, GarlicDiffuserRecipeMaker.getRecipes(registration.getIngredientManager()));
        registration.addRecipes(FOG_DIFFUSER, FogDiffuserRecipeMaker.getRecipes(registration.getIngredientManager()));
        registration.addRecipes(GRINDER_RECIPE, GrinderRecipeMaker.getRecipes(registration.getIngredientManager()));
        registration.addRecipes(BLOOD_SIEVE_CONVERSION, BloodSieveRecipeMaker.getRecipes(registration.getIngredientManager()));
    }

    @Override
    public void registerVanillaCategoryExtensions(@NotNull IVanillaCategoryExtensionRegistration registration) {

    }

    private @NotNull List<IJeiAnvilRecipe> getRepairRecipes(@NotNull IVanillaRecipeFactory factory) {
        List<IJeiAnvilRecipe> recipes = new ArrayList<>();
        Map<Ingredient, List<Item>> items = Maps.newHashMap();
        Ingredient ironIngredient = Tiers.IRON.getRepairIngredient();
        items.put(ironIngredient, Lists.newArrayList(ModItems.HUNTER_AXE_NORMAL.get(), ModItems.HUNTER_AXE_ENHANCED.get(), ModItems.HUNTER_AXE_ULTIMATE.get(), ModItems.BASIC_TECH_CROSSBOW.get(), ModItems.ENHANCED_TECH_CROSSBOW.get(), ModItems.HUNTER_COAT_CHEST_NORMAL.get(), ModItems.HUNTER_COAT_CHEST_ENHANCED.get(), ModItems.HUNTER_COAT_CHEST_ULTIMATE.get(), ModItems.HUNTER_COAT_HEAD_NORMAL.get(), ModItems.HUNTER_COAT_HEAD_ENHANCED.get(), ModItems.HUNTER_COAT_HEAD_ULTIMATE.get(), ModItems.HUNTER_COAT_LEGS_NORMAL.get(), ModItems.HUNTER_COAT_LEGS_ENHANCED.get(), ModItems.HUNTER_COAT_LEGS_ULTIMATE.get(), ModItems.HUNTER_COAT_FEET_NORMAL.get(), ModItems.HUNTER_COAT_FEET_ENHANCED.get(), ModItems.HUNTER_COAT_FEET_ULTIMATE.get()));
        Ingredient stringIngredient = Ingredient.of(Tags.Items.STRINGS);
        items.put(stringIngredient, Lists.newArrayList(ModItems.BASIC_CROSSBOW.get(), ModItems.BASIC_DOUBLE_CROSSBOW.get(), ModItems.ENHANCED_CROSSBOW.get(), ModItems.ENHANCED_DOUBLE_CROSSBOW.get()));
        Ingredient leather = Ingredient.of(Tags.Items.LEATHERS);
        items.put(leather, Lists.newArrayList(ModItems.ARMOR_OF_SWIFTNESS_CHEST_NORMAL.get(), ModItems.ARMOR_OF_SWIFTNESS_CHEST_ENHANCED.get(), ModItems.ARMOR_OF_SWIFTNESS_CHEST_ULTIMATE.get(), ModItems.ARMOR_OF_SWIFTNESS_HEAD_NORMAL.get(), ModItems.ARMOR_OF_SWIFTNESS_HEAD_ENHANCED.get(), ModItems.ARMOR_OF_SWIFTNESS_HEAD_ULTIMATE.get(), ModItems.ARMOR_OF_SWIFTNESS_LEGS_NORMAL.get(), ModItems.ARMOR_OF_SWIFTNESS_LEGS_ENHANCED.get(), ModItems.ARMOR_OF_SWIFTNESS_LEGS_ULTIMATE.get(), ModItems.ARMOR_OF_SWIFTNESS_FEET_NORMAL.get(), ModItems.ARMOR_OF_SWIFTNESS_FEET_ENHANCED.get(), ModItems.ARMOR_OF_SWIFTNESS_FEET_ULTIMATE.get()));
        Ingredient bloodIngot = Ingredient.of(ModItems.BLOOD_INFUSED_IRON_INGOT.get());
        items.put(bloodIngot, Lists.newArrayList(ModItems.HEART_SEEKER_NORMAL.get(), ModItems.HEART_STRIKER_NORMAL.get()));
        Ingredient enhancedBloodIngot = Ingredient.of(ModItems.BLOOD_INFUSED_ENHANCED_IRON_INGOT.get());
        items.put(enhancedBloodIngot, Lists.newArrayList(ModItems.HEART_SEEKER_ENHANCED.get(), ModItems.HEART_SEEKER_ULTIMATE.get(), ModItems.HEART_STRIKER_ENHANCED.get(), ModItems.HEART_STRIKER_ULTIMATE.get()));
        Ingredient human_heart = Ingredient.of(ModItemTags.HEART);
        items.put(human_heart, Lists.newArrayList(ModItems.VAMPIRE_CLOTHING_CROWN.get(), ModItems.VAMPIRE_CLOTHING_HAT.get(), ModItems.VAMPIRE_CLOTHING_LEGS.get(), ModItems.VAMPIRE_CLOTHING_BOOTS.get(), ModItems.VAMPIRE_CLOAK_RED_BLACK.get(), ModItems.VAMPIRE_CLOAK_BLACK_RED.get(), ModItems.VAMPIRE_CLOAK_BLACK_WHITE.get(), ModItems.VAMPIRE_CLOAK_WHITE_BLACK.get(), ModItems.VAMPIRE_CLOAK_BLACK_BLUE.get()));

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

    private @NotNull List<RecipeHolder<CraftingRecipe>> getApplicableOilRecipes() {
        return ModRegistries.OILS.holders()
                .filter(s -> s.value() instanceof IApplicableOil)
                .map(s -> (Holder<IApplicableOil> )(Object)s)
                .flatMap(oil -> BuiltInRegistries.ITEM.stream()
                        .map(Item::getDefaultInstance)
                        .filter(item -> (!(item.getItem() instanceof IFactionExclusiveItem) || IFaction.is(ModFactions.HUNTER, ((IFactionExclusiveItem) item.getItem()).getExclusiveFaction(item))))
                        .filter(item -> oil.value().canBeApplied(item))
                        .map(stack -> new RecipeHolder<CraftingRecipe>(VResourceLocation.mod((oil.unwrapKey().orElseThrow().location().toString() + RegUtil.id(stack.getItem())).replace(':', '_')), new ShapelessRecipe( "", CraftingBookCategory.EQUIPMENT, AppliedOilContent.apply(stack.copy(), oil), NonNullList.of(Ingredient.EMPTY, Ingredient.of(stack), Ingredient.of(OilContent.createItemStack(ModItems.OIL_BOTTLE.get(), oil))))))).toList();
    }

    private @NotNull List<RecipeHolder<CraftingRecipe>> getCleanOilRecipes(RegistryAccess registryAccess) {
        return getApplicableOilRecipes().stream().map(recipe -> {
            ItemStack item = recipe.value().getResultItem(registryAccess);
            IApplicableOil oil = OilUtils.getAppliedOil(item).get();
            return new RecipeHolder<CraftingRecipe>(VResourceLocation.mod(("clean_" + RegUtil.id(oil) + "_from_" + RegUtil.id(item.getItem())).replace(':', '_')),new ShapelessRecipe("", CraftingBookCategory.EQUIPMENT, AppliedOilContent.remove(item.copy()), NonNullList.of(Ingredient.EMPTY, Ingredient.of(Items.PAPER), Ingredient.of(item))));
        }).collect(Collectors.toList());
    }
}

package de.teamlapen.vampirism.common.integration.jei;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.player.task.Task;
import de.teamlapen.vampirism.api.items.IWeaponTableRecipe;
import de.teamlapen.vampirism.api.util.VResourceLocation;
import de.teamlapen.vampirism.client.gui.screens.*;
import de.teamlapen.vampirism.client.gui.screens.diffuser.FogDiffuserScreen;
import de.teamlapen.vampirism.client.gui.screens.diffuser.GarlicDiffuserScreen;
import de.teamlapen.vampirism.common.core.ModBlocks;
import de.teamlapen.vampirism.common.core.ModItems;
import de.teamlapen.vampirism.common.core.ModMenus;
import de.teamlapen.vampirism.common.core.ModRecipes;
import de.teamlapen.vampirism.common.entity.player.tasks.TaskUtil;
import de.teamlapen.vampirism.common.integration.jei.categories.*;
import de.teamlapen.vampirism.common.integration.jei.recipes.*;
import de.teamlapen.vampirism.common.integration.jei.recipes.maker.*;
import de.teamlapen.vampirism.common.integration.jei.subtypes.BloodBottleInterpreter;
import de.teamlapen.vampirism.common.integration.jei.subtypes.BloodContainerInterpreter;
import de.teamlapen.vampirism.common.integration.jei.subtypes.ContainedOilInterpreter;
import de.teamlapen.vampirism.common.inventory.AlchemicalCauldronMenu;
import de.teamlapen.vampirism.common.inventory.WeaponTableMenu;
import de.teamlapen.vampirism.common.recipes.AlchemicalCauldronRecipe;
import de.teamlapen.vampirism.common.recipes.AlchemyTableRecipe;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.types.IRecipeHolderType;
import mezz.jei.api.recipe.types.IRecipeType;
import mezz.jei.api.registration.*;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.stream.Collectors;

@JeiPlugin
public class VampirismJEIPlugin implements IModPlugin {
    public static final IRecipeType<RecipeHolder<IWeaponTableRecipe>> WEAPON_TABLE = createFromModded(ModRecipes.WEAPONTABLE_CRAFTING_TYPE);
    public static final IRecipeType<RecipeHolder<AlchemicalCauldronRecipe>> ALCHEMICAL_CAULDRON = createFromModded(ModRecipes.ALCHEMICAL_CAULDRON_TYPE);
    public static final IRecipeType<Task> TASK = IRecipeType.create("vampirism", "task", Task.class);
    public static final IRecipeType<BlessableRecipe> BLESSING = IRecipeType.create("vampirism", "blessing", BlessableRecipe.class);
    public static final IRecipeType<JEIPotionMix> POTION = IRecipeType.create("vampirism", "potion", JEIPotionMix.class);
    public static final IRecipeType<RecipeHolder<AlchemyTableRecipe>> ALCHEMY_TABLE = createFromModded(ModRecipes.ALCHEMICAL_TABLE_TYPE);
    public static final IRecipeType<GarlicDiffuserRecipe> GARLIC_DIFFUSER = IRecipeType.create(REFERENCE.MODID, "garlic_diffuser", GarlicDiffuserRecipe.class);
    public static final IRecipeType<FogDiffuserRecipe> FOG_DIFFUSER = IRecipeType.create(REFERENCE.MODID, "fog_diffuser", FogDiffuserRecipe.class);
    public static final IRecipeType<GrinderRecipe> GRINDER_RECIPE = IRecipeType.create(REFERENCE.MODID, "grinder", GrinderRecipe.class);
    public static final IRecipeType<BloodSieveRecipe> BLOOD_SIEVE_CONVERSION = IRecipeType.create(REFERENCE.MODID, "blood_sieve", BloodSieveRecipe.class);

    private static final ResourceLocation ID = VResourceLocation.mod("plugin");

    @NotNull
    @Override
    public ResourceLocation getPluginUid() {
        return ID;
    }

    public static <R extends Recipe<?>> IRecipeType<RecipeHolder<R>> createFromModded(DeferredHolder<net.minecraft.world.item.crafting.RecipeType<?>, net.minecraft.world.item.crafting.RecipeType<R>> vanillaRecipeType) {
        return IRecipeHolderType.create(vanillaRecipeType.getId());
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
        registration.addRecipeTransferHandler(AlchemicalCauldronMenu.class, ModMenus.ALCHEMICAL_CAULDRON.get(), RecipeTypes.BLASTING, 3, 1, 4, 36);
        registration.addRecipeTransferHandler(WeaponTableMenu.class, ModMenus.WEAPON_TABLE.get(), WEAPON_TABLE, 1, 16, 17, 36);
    }

    @Override
    public void registerItemSubtypes(@NotNull ISubtypeRegistration registration) {
        registration.registerSubtypeInterpreter(ModItems.OIL_BOTTLE.get(), new ContainedOilInterpreter());
        registration.registerSubtypeInterpreter(ModItems.BLOOD_BOTTLE.get(), new BloodBottleInterpreter());
        registration.registerSubtypeInterpreter(ModBlocks.BLOOD_CONTAINER.asItem(), new BloodContainerInterpreter());
    }

    @Override
    public void registerRecipes(@NotNull IRecipeRegistration registration) {
        var recipes = VampirismMod.getServices().recipes().getRecipes();
        var level = Minecraft.getInstance().level;
        registration.addRecipes(ALCHEMICAL_CAULDRON, recipes.byType(ModRecipes.ALCHEMICAL_CAULDRON_TYPE.get()).stream().toList());
        registration.addRecipes(WEAPON_TABLE, recipes.byType(ModRecipes.WEAPONTABLE_CRAFTING_TYPE.get()).stream().toList());
        registration.addRecipes(TASK, TaskUtil.getItemRewardTasks(level.registryAccess()));
        registration.addRecipes(POTION, VampirismAPI.extendedBrewingRecipeRegistry().getPotionMixes().stream().map(JEIPotionMix::createFromMix).flatMap(Collection::stream).collect(Collectors.toList()));
        registration.addRecipes(RecipeTypes.ANVIL, RepairRecipeMaker.getRecipes(registration.getVanillaRecipeFactory(), registration.getIngredientManager()));
        registration.addRecipes(ALCHEMY_TABLE, recipes.byType(ModRecipes.ALCHEMICAL_TABLE_TYPE.get()).stream().toList());
        registration.addRecipes(RecipeTypes.CRAFTING, OilRecipeMaker.getRecipes(registration.getIngredientManager()));
        registration.addRecipes(BLESSING, BlessableRecipeMaker.getRecipes(registration.getIngredientManager()));
        registration.addRecipes(GARLIC_DIFFUSER, GarlicDiffuserRecipeMaker.getRecipes(registration.getIngredientManager()));
        registration.addRecipes(FOG_DIFFUSER, FogDiffuserRecipeMaker.getRecipes(registration.getIngredientManager()));
        registration.addRecipes(GRINDER_RECIPE, GrinderRecipeMaker.getRecipes(registration.getIngredientManager()));
        registration.addRecipes(BLOOD_SIEVE_CONVERSION, BloodSieveRecipeMaker.getRecipes(registration.getIngredientManager()));
    }

    @Override
    public void registerVanillaCategoryExtensions(@NotNull IVanillaCategoryExtensionRegistration registration) {

    }
}

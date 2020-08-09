package de.teamlapen.vampirism.modcompat.jei;

import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.client.gui.AlchemicalCauldronScreen;
import de.teamlapen.vampirism.client.gui.WeaponTableScreen;
import de.teamlapen.vampirism.core.ModRecipes;
import de.teamlapen.vampirism.inventory.container.AlchemicalCauldronContainer;
import de.teamlapen.vampirism.inventory.container.WeaponTableContainer;
import de.teamlapen.vampirism.player.tasks.TaskUtil;
import de.teamlapen.vampirism.util.REFERENCE;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaRecipeCategoryUid;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.registration.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.Collection;
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
        registration.addRecipeClickArea(AlchemicalCauldronScreen.class,80,34,20,15,ALCHEMICAL_CAULDRON_RECIPE_UID);
        registration.addRecipeClickArea(WeaponTableScreen.class, 114, 46, 20, 15, WEAPON_TABLE_RECIPE_ID);
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        ClientWorld world = Minecraft.getInstance().world;
        RecipeManager recipeManager = world.getRecipeManager();
        registration.addRecipes(recipeManager.getRecipes(ModRecipes.ALCHEMICAL_CAULDRON_TYPE).values(),ALCHEMICAL_CAULDRON_RECIPE_UID);
        registration.addRecipes(recipeManager.getRecipes(ModRecipes.WEAPONTABLE_CRAFTING_TYPE).values(), WEAPON_TABLE_RECIPE_ID);
        registration.addRecipes(TaskUtil.getItemRewardTasks(), TASK_RECIPE_UID);
        registration.addRecipes(VampirismAPI.extendedBrewingRecipeRegistry().getPotionMixes().stream().map(JEIPotionMix::createFromMix).flatMap(Collection::stream).collect(Collectors.toList()), POTION_RECIPE_UID);
    }

    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
        registration.addRecipeTransferHandler(AlchemicalCauldronContainer.class,ALCHEMICAL_CAULDRON_RECIPE_UID,0,2,4,36);
        registration.addRecipeTransferHandler(AlchemicalCauldronContainer.class, VanillaRecipeCategoryUid.FUEL,3,1,4,36);
        registration.addRecipeTransferHandler(WeaponTableContainer.class, WEAPON_TABLE_RECIPE_ID, 1, 16, 17, 36);
    }

    @Override
    public void registerVanillaCategoryExtensions(IVanillaCategoryExtensionRegistration registration) {

    }
}

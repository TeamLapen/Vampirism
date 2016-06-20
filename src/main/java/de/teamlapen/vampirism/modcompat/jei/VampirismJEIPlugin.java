package de.teamlapen.vampirism.modcompat.jei;

import de.teamlapen.vampirism.client.gui.GuiHunterWeaponTable;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.inventory.HunterWeaponCraftingManager;
import de.teamlapen.vampirism.inventory.HunterWeaponTableContainer;
import mezz.jei.api.*;
import mezz.jei.api.recipe.transfer.IRecipeTransferRegistry;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nonnull;

/**
 * Plugin for Just Enough Items
 */
@JEIPlugin
public class VampirismJEIPlugin extends BlankModPlugin {
    public static final String HUNTER_WEAPON_RECIPE_UID = "vampirism.hunter_weapon";

    @Override
    public void register(@Nonnull IModRegistry registry) {
        IJeiHelpers jeiHelpers = registry.getJeiHelpers();

        jeiHelpers.getItemBlacklist().addItemToBlacklist(new ItemStack(ModBlocks.fluidBlood));
        jeiHelpers.getItemBlacklist().addItemToBlacklist(new ItemStack(ModItems.bloodPotion, 1, OreDictionary.WILDCARD_VALUE));
        IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

        registry.addRecipeCategories(new HunterWeaponRecipeCategory(guiHelper));
        registry.addRecipeHandlers(new ShapedHunterWeaponRecipesHandler());
        registry.addRecipeHandlers(new ShapelessHunterWeaponRecipeHandler());
        registry.addRecipeClickArea(GuiHunterWeaponTable.class, 113, 46, 28, 23, HUNTER_WEAPON_RECIPE_UID);
        registry.addRecipeCategoryCraftingItem(new ItemStack(ModBlocks.weaponTable), HUNTER_WEAPON_RECIPE_UID);
        registry.addRecipes(HunterWeaponCraftingManager.getInstance().getRecipes());

        IRecipeTransferRegistry recipeTransferRegistry = registry.getRecipeTransferRegistry();
        recipeTransferRegistry.addRecipeTransferHandler(HunterWeaponTableContainer.class, HUNTER_WEAPON_RECIPE_UID, 1, 16, 17, 36);

        //TODO add recipe handler for hunter table
    }
}

package de.teamlapen.vampirism.core;

import de.teamlapen.vampirism.recipes.RecipeVampireSword;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.registries.IForgeRegistry;

/**
 * Handles all recipe registrations and reference.
 */
public class ModRecipes {

    /**
     * _X_ <br>
     * XYX
     * <p>
     * X = type(blood_iron)
     * Y = HeartSeeker
     */
    private static final RecipeVampireSword recipeHeartSeeker = new RecipeVampireSword("heartseeker", ModItems.heart_seeker) {
    };

    /**
     * XXX <br>
     * XYX
     * <p>
     * X = type(blood_iron)
     * Y = HeartStriker
     */
    private static final RecipeVampireSword recipeHeartStriker = new RecipeVampireSword("heartstriker", ModItems.heart_striker) {

        protected boolean check(InventoryCrafting inv, Item item, int i, int j) {

            if (inv.getStackInRowAndColumn(i, j - 1).getItem() == item && inv.getStackInRowAndColumn(i - 1, j).getItem() == item && inv.getStackInRowAndColumn(i + 1, j).getItem() == item && inv.getStackInRowAndColumn(i - 1, j - 1).getItem() == item && inv.getStackInRowAndColumn(i + 1, j - 1).getItem() == item) {
                resultItem = getCraftingResult(inv);
                return true;
            }
            return false;
        }
    };

    static void registerRecipes(IForgeRegistry<IRecipe> registry) {

        registry.register(recipeHeartSeeker);
        registry.register(recipeHeartStriker);
    }
}

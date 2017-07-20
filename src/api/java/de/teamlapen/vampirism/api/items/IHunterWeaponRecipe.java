package de.teamlapen.vampirism.api.items;

import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

/**
 * Recipe that can be used in the hunter weapon crafting table
 */
public interface IHunterWeaponRecipe {


    /**
     * Returns an Item that is the result of this recipe
     */
    default ItemStack getCraftingResult(InventoryCrafting inv) {
        return getRecipeOutput().copy();
    }

    default NonNullList<Ingredient> getIngredients() {
        return NonNullList.create();
    }

    /**
     * @return The hunter level required to craft this
     */
    int getMinHunterLevel();

    ItemStack getRecipeOutput();

    default NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
        return net.minecraftforge.common.ForgeHooks.defaultRecipeGetRemainingItems(inv);
    }

    /**
     * Measured in 1/5 buckets. Min value=0
     *
     * @return The amount of lava required for this recipe.
     */
    int getRequiredLavaUnits();

    /**
     * @return The skills that have to be unlocked to craft this. Can be empty
     */
    @Nonnull
    ISkill[] getRequiredSkills();

    default boolean isHidden() {
        return false;
    }

    /**
     * Used to check if a recipe matches current crafting inventory
     */
    boolean matches(InventoryCrafting inv, World worldIn);

}

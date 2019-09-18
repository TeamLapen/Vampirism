package de.teamlapen.vampirism.api.items;

import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;

import javax.annotation.Nullable;

/**
 * Recipe that can be used in the hunter weapon crafting table
 */
public interface IWeaponTableRecipe extends IRecipe<CraftingInventory> {

    /**
     * Returns an Item that is the result of this recipe
     */
    default ItemStack getCraftingResult(CraftingInventory inv) {
        return getRecipeOutput().copy();
    }

    default NonNullList<Ingredient> getIngredients() {
        return NonNullList.create();
    }

    /**
     * Measured in 1/5 buckets. Min value=0
     *
     * @return The amount of lava required for this recipe.
     */
    int getRequiredLavaUnits();

    /**
     * @return The hunter level required to craft this
     */
    int getRequiredLevel();

    /**
     * @return The skills that have to be unlocked to craft this. Can be empty
     */
    @Nullable
    ISkill[] getRequiredSkills();
}

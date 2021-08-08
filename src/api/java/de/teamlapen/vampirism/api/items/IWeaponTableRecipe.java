package de.teamlapen.vampirism.api.items;

import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.core.NonNullList;

import javax.annotation.Nonnull;

/**
 * Recipe that can be used in the hunter weapon crafting table
 */
public interface IWeaponTableRecipe extends Recipe<CraftingContainer> {

    /**
     * Returns an Item that is the result of this recipe
     */
    default ItemStack assemble(CraftingContainer inv) {
        return getResultItem().copy();
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
    @Nonnull
    ISkill[] getRequiredSkills();
}

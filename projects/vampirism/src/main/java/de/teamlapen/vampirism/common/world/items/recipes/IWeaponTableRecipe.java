package de.teamlapen.vampirism.common.world.items.recipes;

import de.teamlapen.faction.api.factions.skills.ISkill;
import de.teamlapen.vampirism.common.core.ModRecipes;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;

import java.util.List;

public interface IWeaponTableRecipe extends Recipe<CraftingInput> {

    @Override
    default RecipeType<IWeaponTableRecipe> getType() {
        return ModRecipes.WEAPONTABLE_CRAFTING_TYPE.get();
    }

    @Override
    RecipeSerializer<? extends IWeaponTableRecipe> getSerializer();

    @Override
    default RecipeBookCategory recipeBookCategory() {
        return ModRecipes.WEAPON_TABLE_CATEGORY.get();
    }

    default NonNullList<ItemStack> getRemainingItems(CraftingInput input) {
        return CraftingRecipe.defaultCraftingReminder(input);
    }

    /**
     * Measured in 1/5 buckets. Min value = 0
     */
    int getRequiredLavaUnits();

    /**
     * @return The hunter level required to craft this
     */
    int getRequiredLevel();

    /**
     * @return The skills that have to be unlocked to craft this. Can be empty
     */
    List<Holder<ISkill<?>>> getRequiredSkills();

    List<Ingredient> getIngredients();
}

package de.teamlapen.vampirism.api.items;

import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.entity.player.skills.ISkillHandler;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Crafting manager interface for the hunter weapon crafting table
 */
public interface IHunterWeaponCraftingManager {
    /**
     * Add a recipe for the hunter weapon crafting table
     *
     * @param output           The resulting itemstack
     * @param reqLevel         The required hunter level >=0
     * @param reqSkill         A skill that is required to craft this. Can be null
     * @param reqLava          The number of lava units required. (One unit equals 200mB). Max is 5
     * @param recipeComponents The components in the same way as vanilla crafting.
     * @return The created and registered recipe
     */
    IHunterWeaponRecipe addRecipe(ItemStack output, int reqLevel, @Nullable ISkill<IHunterPlayer> reqSkill, int reqLava, Object... recipeComponents);

    /**
     * Add a recipe for the hunter weapon crafting table
     *
     * @param output           The resulting itemstack
     * @param reqLevel         The required hunter level >=0
     * @param reqSkills        An array of required skills. Can be empty but not null
     * @param reqLava          The number of lava units required. (One unit equals 200mB). Max is 5
     * @param recipeComponents The components in the same way as vanilla crafting.
     * @return The created and registered recipe
     */
    IHunterWeaponRecipe addRecipe(ItemStack output, int reqLevel, @Nonnull ISkill<IHunterPlayer>[] reqSkills, int reqLava, Object... recipeComponents);

    /**
     * Adds the given recipe
     */
    void addRecipe(IHunterWeaponRecipe recipe);

    /**
     * Add a shapeless recipe for the hunter weapon crafting table
     *
     * @param output           The resulting itemstack
     * @param reqLevel         The required hunter level >=0
     * @param reqSkills        An array of required skills. Can be empty but not null
     * @param reqLava          The number of lava units required. (One unit equals 200mB). Max is 5
     * @param recipeComponents The required components
     * @return The created and registered recipe
     */
    IHunterWeaponRecipe addShapelessRecipe(ItemStack output, int reqLevel, @Nonnull ISkill<IHunterPlayer>[] reqSkills, int reqLava, Object... recipeComponents);

    /**
     * Add a shapeless recipe for the hunter weapon crafting table
     *
     * @param output           The resulting itemstack
     * @param reqLevel         The required hunter level >=0
     * @param reqSkill         A skill that is required to craft this. Can be null
     * @param reqLava          The number of lava units required. (One unit equals 200mB)
     * @param recipeComponents The required components
     * @return The created and registered recipe
     */
    IHunterWeaponRecipe addShapelessRecipe(ItemStack output, int reqLevel, @Nullable ISkill<IHunterPlayer> reqSkill, int reqLava, Object... recipeComponents);

    @Nullable
    IHunterWeaponRecipe findMatchingRecipe(InventoryCrafting craftMatrix, World world, int playerLevel, ISkillHandler<IHunterPlayer> skillHandler, int lava);

    @Nullable
    ItemStack findMatchingRecipeResult(InventoryCrafting craftMatrix, World world, int playerLevel, ISkillHandler<IHunterPlayer> skillHandler, int lava);

    NonNullList<ItemStack> getRemainingItems(InventoryCrafting craftMatrix, World world, int playerLevel, ISkillHandler<IHunterPlayer> skillHandler, int lava);
}

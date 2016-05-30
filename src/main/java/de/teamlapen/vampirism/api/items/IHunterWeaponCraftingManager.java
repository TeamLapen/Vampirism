package de.teamlapen.vampirism.api.items;

import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.entity.player.skills.ISkillHandler;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * Crafting manager interface for the hunter weapon crafting table
 */
public interface IHunterWeaponCraftingManager {
    IHunterWeaponRecipe addRecipe(ItemStack output, int reqLevel, @Nullable ISkill<IHunterPlayer> reqSkill, Object... recipeComponents);

    void addRecipe(IHunterWeaponRecipe recipe);

    @Nullable
    ItemStack findMatchingRecipe(InventoryCrafting craftMatrix, World world, int playerLevel, ISkillHandler<IHunterPlayer> skillHandler);

    ItemStack[] getRemainingItems(InventoryCrafting craftMatrix, World world, int playerLevel, ISkillHandler<IHunterPlayer> skillHandler);
}

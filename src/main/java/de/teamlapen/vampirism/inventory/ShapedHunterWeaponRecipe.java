package de.teamlapen.vampirism.inventory;

import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.items.IHunterWeaponRecipe;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * Shaped recipe for the hunter weapon table. Only supports 4x4 recipes
 */
public class ShapedHunterWeaponRecipe extends ShapedRecipes implements IHunterWeaponRecipe {
    private final int requiredHunterLevel;
    private final ISkill<IHunterPlayer> requiredHunterSkill;

    public ShapedHunterWeaponRecipe(ItemStack[] p_i1917_3_, ItemStack output, int requiredHunterLevel, @Nullable ISkill<IHunterPlayer> requiredHunterSkill) {
        super(4, 4, p_i1917_3_, output);
        this.requiredHunterLevel = requiredHunterLevel;
        this.requiredHunterSkill = requiredHunterSkill;
    }

    @Override
    public int getMinHunterLevel() {
        return requiredHunterLevel;
    }

    @Nullable
    @Override
    public ISkill<IHunterPlayer> getRequiredSkill() {
        return requiredHunterSkill;
    }

    @Override
    public boolean matches(InventoryCrafting inv, World worldIn) {
        for (int i = 0; i < 4; ++i) {
            for (int j = 0; j < 4; ++j) {

                ItemStack itemstack = this.recipeItems[i + j * this.recipeWidth];


                ItemStack itemstack1 = inv.getStackInRowAndColumn(i, j);

                if (itemstack1 != null || itemstack != null) {
                    if (itemstack1 == null || itemstack == null) {
                        return false;
                    }

                    if (itemstack.getItem() != itemstack1.getItem()) {
                        return false;
                    }

                    if (itemstack.getMetadata() != 32767 && itemstack.getMetadata() != itemstack1.getMetadata()) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

}

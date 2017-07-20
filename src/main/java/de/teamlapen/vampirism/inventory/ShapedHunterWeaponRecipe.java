package de.teamlapen.vampirism.inventory;

import de.teamlapen.lib.lib.util.ItemStackUtil;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.items.IHunterWeaponRecipe;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

/**
 * Shaped recipe for the hunter weapon table.
 */
public class ShapedHunterWeaponRecipe implements IHunterWeaponRecipe {
    public final int recipeWidth;
    public final int recipeHeight;
    public final ItemStack[] recipeItems;
    private final int requiredHunterLevel;
    private final
    @Nonnull
    ISkill[] requiredHunterSkills;
    private final int requiredLavaUnits;
    private final @Nonnull
    ItemStack recipeOutput;

    public ShapedHunterWeaponRecipe(int width, int height, ItemStack[] input, @Nonnull ItemStack output, int requiredHunterLevel, @Nonnull ISkill[] requiredHunterSkills, int requiredLavaUnits) {
        assert !ItemStackUtil.isEmpty(output);
        this.recipeWidth = width;
        this.recipeHeight = height;
        recipeItems = input;
        recipeOutput = output;
        this.requiredHunterLevel = requiredHunterLevel;
        this.requiredHunterSkills = requiredHunterSkills;
        this.requiredLavaUnits = requiredLavaUnits;
    }




    @Override
    public int getMinHunterLevel() {
        return requiredHunterLevel;
    }

    @Nonnull
    public ItemStack getRecipeOutput() {
        return this.recipeOutput;
    }




    @Override
    public int getRequiredLavaUnits() {
        return requiredLavaUnits;
    }

    @Nonnull
    @Override
    public ISkill[] getRequiredSkills() {
        return requiredHunterSkills;
    }

    @Override
    public boolean matches(InventoryCrafting inv, World worldIn) {
        for (int x = 0; x <= 4 - this.recipeWidth; ++x) {
            for (int y = 0; y <= 4 - this.recipeHeight; ++y) {
                if (this.checkMatch(inv, x, y, true)) {
                    return true;
                }

                if (this.checkMatch(inv, x, y, false)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean checkMatch(InventoryCrafting inv, int startRow, int startColumn, boolean flip) {
        for (int x = 0; x < 4; x++) {
            for (int y = 0; y < 4; y++) {
                int k = x - startRow;
                int l = y - startColumn;
                ItemStack itemStack = ItemStackUtil.getEmptyStack();
                if (k >= 0 && l >= 0 && k < recipeWidth && l < recipeHeight) {
                    if (flip) {
                        itemStack = this.recipeItems[this.recipeWidth - k - 1 + l * this.recipeWidth];
                    } else {
                        itemStack = this.recipeItems[k + l * recipeWidth];
                    }
                }
                ItemStack itemstack1 = inv.getStackInRowAndColumn(x, y);

                if (!ItemStackUtil.isEmpty(itemStack) || !ItemStackUtil.isEmpty(itemstack1)) {
                    if (ItemStackUtil.isEmpty(itemStack) || ItemStackUtil.isEmpty(itemstack1)) {
                        return false;
                    }

                    if (itemStack.getItem() != itemstack1.getItem()) {
                        return false;
                    }

                    if (itemStack.getMetadata() != 32767 && itemStack.getMetadata() != itemstack1.getMetadata()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

}

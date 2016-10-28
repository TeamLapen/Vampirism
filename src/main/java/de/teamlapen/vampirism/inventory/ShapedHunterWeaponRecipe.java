package de.teamlapen.vampirism.inventory;

import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.items.IHunterWeaponRecipe;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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
    ISkill<IHunterPlayer>[] requiredHunterSkills;
    private final int requiredLavaUnits;
    private final ItemStack recipeOutput;

    public ShapedHunterWeaponRecipe(int width, int height, ItemStack[] input, ItemStack output, int requiredHunterLevel, @Nonnull ISkill<IHunterPlayer>[] requiredHunterSkills, int requiredLavaUnits) {
        this.recipeWidth = width;
        this.recipeHeight = height;
        recipeItems = input;
        recipeOutput = output;
        this.requiredHunterLevel = requiredHunterLevel;
        this.requiredHunterSkills = requiredHunterSkills;
        this.requiredLavaUnits = requiredLavaUnits;
    }


    /**
     * Returns an Item that is the result of this recipe
     */
    @Nullable
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        ItemStack itemstack = this.getRecipeOutput().copy();

        return itemstack;
    }

    @Override
    public int getMinHunterLevel() {
        return requiredHunterLevel;
    }

    @Nullable
    public ItemStack getRecipeOutput() {
        return this.recipeOutput;
    }

    /**
     * Returns the size of the recipe area
     */
    public int getRecipeSize() {
        return this.recipeWidth * this.recipeHeight;
    }

    public ItemStack[] getRemainingItems(InventoryCrafting inv) {
        ItemStack[] aitemstack = new ItemStack[inv.getSizeInventory()];

        for (int i = 0; i < aitemstack.length; ++i) {
            ItemStack itemstack = inv.getStackInSlot(i);
            aitemstack[i] = net.minecraftforge.common.ForgeHooks.getContainerItem(itemstack);
        }

        return aitemstack;
    }

    @Override
    public int getRequiredLavaUnits() {
        return requiredLavaUnits;
    }

    @Nonnull
    @Override
    public ISkill<IHunterPlayer>[] getRequiredSkills() {
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
                ItemStack itemStack = null;
                if (k >= 0 && l >= 0 && k < recipeWidth && l < recipeHeight) {
                    if (flip) {
                        itemStack = this.recipeItems[this.recipeWidth - k - 1 + l * this.recipeWidth];
                    } else {
                        itemStack = this.recipeItems[k + l * recipeWidth];
                    }
                }
                ItemStack itemstack1 = inv.getStackInRowAndColumn(x, y);

                if (itemstack1 != null || itemStack != null) {
                    if (itemstack1 == null || itemStack == null) {
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

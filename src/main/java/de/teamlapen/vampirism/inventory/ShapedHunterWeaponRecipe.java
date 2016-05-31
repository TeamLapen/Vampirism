package de.teamlapen.vampirism.inventory;

import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.items.IHunterWeaponRecipe;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * Shaped recipe for the hunter weapon table. Only supports 4x4 recipes
 */
public class ShapedHunterWeaponRecipe implements IHunterWeaponRecipe {
    public final int recipeWidth = 4;
    public final int recipeHeight = 4;
    public final ItemStack[] recipeItems;
    private final int requiredHunterLevel;
    private final ISkill<IHunterPlayer> requiredHunterSkill;
    private final int requiredLavaUnits;
    private final ItemStack recipeOutput;

    public ShapedHunterWeaponRecipe(ItemStack[] input, ItemStack output, int requiredHunterLevel, @Nullable ISkill<IHunterPlayer> requiredHunterSkill, int requiredLavaUnits) {
        recipeItems = input;
        recipeOutput = output;
        this.requiredHunterLevel = requiredHunterLevel;
        this.requiredHunterSkill = requiredHunterSkill;
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

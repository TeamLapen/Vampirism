package de.teamlapen.vampirism.recipes;

import de.teamlapen.vampirism.api.items.IItemWithTier;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.items.IItemWithTierNBTImpl;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

public abstract class RecipeVampireSword extends net.minecraftforge.registries.IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {

    protected ItemStack resultItem = ItemStack.EMPTY;

    protected Item sword;

    public RecipeVampireSword(String regName, Item sword) {
        this.setRegistryName(regName);
        this.sword = sword;
    }

    @Override
    public boolean canFit(int width, int height) {
        return width >= 3 && height >= 3;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        ItemStack itemstack = inv.getStackInRowAndColumn(1, 1).isEmpty() ? inv.getStackInRowAndColumn(2, 1) : inv.getStackInRowAndColumn(1, 1);
        ItemStack itemstack1 = itemstack.copy();
        itemstack1.setItemDamage(0);
        return itemstack1;
    }

    @Override
    public ItemStack getRecipeOutput() {

        return this.resultItem;
    }

    @Override
    public boolean matches(InventoryCrafting inv, World worldIn) {
        resultItem = ItemStack.EMPTY;
        if (inv.getWidth() == 3 && inv.getHeight() == 3) {
            for (int i = 0; i < inv.getWidth(); ++i) {
                for (int j = 0; j < inv.getHeight(); ++j) {
                    ItemStack itemstack = inv.getStackInRowAndColumn(i, j);

                    if (itemstack.isEmpty()) {
                        continue;
                    }

                    Item item = itemstack.getItem();

                    if (i == 1 && (j == 1 || j == 2)) {
                        if (item == sword) {
                            IItemWithTier.TIER tier = IItemWithTierNBTImpl.getTierStatic(itemstack);
                            Item item1;
                            switch (tier) {
                                case NORMAL:
                                    item1 = ModItems.blood_infused_iron_ingot;
                                    break;
                                case ENHANCED:
                                    item1 = ModItems.blood_infused_enhanced_iron_ingot;
                                    break;
                                default:
                                    return false;
                            }
                            if (check(inv, item1, i, j)) return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * checks for the other ingredients than vampire sword
     */
    protected boolean check(InventoryCrafting inv, Item item, int i, int j) {
        if (inv.getStackInRowAndColumn(i, j - 1).getItem() == item && inv.getStackInRowAndColumn(i - 1, j).getItem() == item && inv.getStackInRowAndColumn(i + 1, j).getItem() == item) {
            resultItem = getCraftingResult(inv);
            return true;
        }
        return false;
    }

}
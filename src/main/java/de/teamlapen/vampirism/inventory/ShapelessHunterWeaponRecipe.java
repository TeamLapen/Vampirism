package de.teamlapen.vampirism.inventory;

import com.google.common.collect.Lists;
import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.items.IHunterWeaponRecipe;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * Shapeless recipe for the hunter weapon table
 */
public class ShapelessHunterWeaponRecipe implements IHunterWeaponRecipe {

    public final List<ItemStack> recipeItems;
    private final int requiredHunterLevel;
    private final ISkill<IHunterPlayer>[] requiredHunterSkills;
    private final int requiredLavaUnits;
    private final ItemStack recipeOutput;

    public ShapelessHunterWeaponRecipe(List<ItemStack> recipeItems, ItemStack recipeOutput, int requiredHunterLevel, ISkill<IHunterPlayer>[] requiredHunterSkills, int requiredLavaUnits) {
        this.recipeItems = recipeItems;
        this.requiredHunterLevel = requiredHunterLevel;
        this.requiredHunterSkills = requiredHunterSkills;
        this.requiredLavaUnits = requiredLavaUnits;
        this.recipeOutput = recipeOutput;
    }

    @Nullable
    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        return recipeOutput.copy();
    }

    @Override
    public int getMinHunterLevel() {
        return requiredHunterLevel;
    }

    @Nullable
    @Override
    public ItemStack getRecipeOutput() {
        return recipeOutput;
    }

    @Override
    public int getRecipeSize() {
        return recipeItems.size();
    }

    @Override
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
        List<ItemStack> list = Lists.newArrayList(this.recipeItems);

        for (int i = 0; i < inv.getHeight(); ++i) {
            for (int j = 0; j < inv.getWidth(); ++j) {
                ItemStack itemstack = inv.getStackInRowAndColumn(j, i);

                if (itemstack != null) {
                    boolean flag = false;

                    for (ItemStack itemstack1 : list) {
                        if (itemstack.getItem() == itemstack1.getItem() && (itemstack1.getMetadata() == 32767 || itemstack.getMetadata() == itemstack1.getMetadata())) {
                            flag = true;
                            list.remove(itemstack1);
                            break;
                        }
                    }

                    if (!flag) {
                        return false;
                    }
                }
            }
        }

        return list.isEmpty();
    }
}

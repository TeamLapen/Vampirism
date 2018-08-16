package de.teamlapen.vampirism.inventory;

import com.google.common.collect.Lists;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.items.IHunterWeaponRecipe;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Shapeless recipe for the hunter weapon table
 */
public class ShapelessHunterWeaponRecipe implements IHunterWeaponRecipe {

    public final List<ItemStack> recipeItems;
    private final int requiredHunterLevel;
    private final ISkill[] requiredHunterSkills;
    private final int requiredLavaUnits;
    private final @Nonnull
    ItemStack recipeOutput;

    public ShapelessHunterWeaponRecipe(List<ItemStack> recipeItems, @Nonnull ItemStack recipeOutput, int requiredHunterLevel, ISkill[] requiredHunterSkills, int requiredLavaUnits) {
        assert !recipeOutput.isEmpty();
        this.recipeItems = recipeItems;
        this.requiredHunterLevel = requiredHunterLevel;
        this.requiredHunterSkills = requiredHunterSkills;
        this.requiredLavaUnits = requiredLavaUnits;
        this.recipeOutput = recipeOutput;
    }


    @Override
    public int getMinHunterLevel() {
        return requiredHunterLevel;
    }

    @Nonnull
    @Override
    public ItemStack getRecipeOutput() {
        return recipeOutput;
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
        List<ItemStack> list = Lists.newArrayList(this.recipeItems);

        for (int i = 0; i < inv.getHeight(); ++i) {
            for (int j = 0; j < inv.getWidth(); ++j) {
                ItemStack itemstack = inv.getStackInRowAndColumn(j, i);

                if (!itemstack.isEmpty()) {
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

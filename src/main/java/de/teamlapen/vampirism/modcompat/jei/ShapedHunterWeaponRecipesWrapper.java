package de.teamlapen.vampirism.modcompat.jei;

import com.google.common.collect.Lists;
import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.blocks.BlockWeaponTable;
import de.teamlapen.vampirism.inventory.ShapedHunterWeaponRecipe;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


/**
 * Wraps {@link ShapedHunterWeaponRecipe}. Draws info about the required level and skill as well as an lava bucket icon if lava is required.
 */
public class ShapedHunterWeaponRecipesWrapper extends BlankRecipeWrapper {
    private static final ItemStack lavaStack = new ItemStack(Items.LAVA_BUCKET);
    private
    @Nonnull
    final ShapedHunterWeaponRecipe recipe;

    public ShapedHunterWeaponRecipesWrapper(@Nonnull ShapedHunterWeaponRecipe recipe) {
        this.recipe = recipe;
    }

    @Override
    public void drawInfo(@Nonnull Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
        if (recipe.getRequiredLavaUnits() > 0) {
            minecraft.getRenderItem().renderItemIntoGUI(lavaStack, 83, 13);
        }
        int x = 2;
        int y = 80;
        if (recipe.getMinHunterLevel() > 0) {
            String level = UtilLib.translateToLocalFormatted("gui.vampirism.hunter_weapon_table.level", recipe.getMinHunterLevel());

            minecraft.fontRendererObj.drawString(level, x, y, Color.gray.getRGB());
            y += minecraft.fontRendererObj.FONT_HEIGHT + 2;
        }
        if (recipe.getRequiredSkill() != null) {
            String skill = UtilLib.translateToLocalFormatted("gui.vampirism.hunter_weapon_table.skill", UtilLib.translateToLocal(recipe.getRequiredSkill().getUnlocalizedName()));
            minecraft.fontRendererObj.drawSplitString(skill, x, y, 132, Color.gray.getRGB());

        }
    }

    @Nonnull
    @Override
    public List<FluidStack> getFluidInputs() {
        if (recipe.getRequiredLavaUnits() > 0) {
            List<FluidStack> l = Lists.newArrayList();
            l.add(new FluidStack(FluidRegistry.LAVA, recipe.getRequiredLavaUnits() * BlockWeaponTable.MB_PER_META));
            return l;
        }

        return Collections.emptyList();
    }

    @Nonnull
    @Override
    public List getInputs() {
        return Arrays.asList(recipe.recipeItems);
    }

    @Nonnull
    @Override
    public List<ItemStack> getOutputs() {
        return Collections.singletonList(recipe.getRecipeOutput());
    }
}

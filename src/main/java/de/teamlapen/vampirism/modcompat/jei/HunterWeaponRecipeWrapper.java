package de.teamlapen.vampirism.modcompat.jei;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.items.IWeaponTableRecipe;
import de.teamlapen.vampirism.blocks.BlockWeaponTable;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.awt.*;

/**
 * Base class for hunter weapon recipe wrapper
 */
public abstract class HunterWeaponRecipeWrapper extends BlankRecipeWrapper {
    private static final ItemStack lavaStack = new ItemStack(Items.LAVA_BUCKET);

    private
    @Nonnull
    final IWeaponTableRecipe recipe;

    protected HunterWeaponRecipeWrapper(@Nonnull IWeaponTableRecipe recipe) {
        this.recipe = recipe;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void drawInfo(@Nonnull Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
        if (recipe.getRequiredLavaUnits() > 0) {
            minecraft.getRenderItem().renderItemIntoGUI(lavaStack, 83, 13);
        }
        int x = 2;
        int y = 80;
        if (recipe.getMinHunterLevel() > 1) {
            String level = UtilLib.translateFormatted("gui.vampirism.hunter_weapon_table.level", recipe.getMinHunterLevel());

            minecraft.fontRenderer.drawString(level, x, y, Color.gray.getRGB());
            y += minecraft.fontRenderer.FONT_HEIGHT + 2;
        }
        if (recipe.getRequiredSkills().length > 0) {
            StringBuilder skills = new StringBuilder();
            for (ISkill skill : recipe.getRequiredSkills()) {
                skills.append(UtilLib.translate(skill.getUnlocalizedName())).append(" ");

            }
            String skillText = UtilLib.translateFormatted("gui.vampirism.hunter_weapon_table.skill", skills.toString());
            minecraft.fontRenderer.drawSplitString(skillText, x, y, 132, Color.gray.getRGB());


        }
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        if (recipe.getRequiredLavaUnits() > 0) {
            FluidStack fluidInput = new FluidStack(FluidRegistry.LAVA, recipe.getRequiredLavaUnits() * BlockWeaponTable.MB_PER_META);

            ingredients.setInput(FluidStack.class, fluidInput);
        }

    }
}

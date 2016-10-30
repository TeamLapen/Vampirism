package de.teamlapen.vampirism.modcompat.jei;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.items.IHunterWeaponRecipe;
import de.teamlapen.vampirism.blocks.BlockWeaponTable;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import java.awt.*;

/**
 * Base class for hunter weapon recipe wrapper
 */
public abstract class HunterWeaponRecipeWrapper extends BlankRecipeWrapper {
    private static final ItemStack lavaStack = new ItemStack(Items.LAVA_BUCKET);

    private
    @Nonnull
    final IHunterWeaponRecipe recipe;

    protected HunterWeaponRecipeWrapper(@Nonnull IHunterWeaponRecipe recipe) {
        this.recipe = recipe;
    }

    @Override
    public void drawInfo(@Nonnull Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
        if (recipe.getRequiredLavaUnits() > 0) {
            minecraft.getRenderItem().renderItemIntoGUI(lavaStack, 83, 13);
        }
        int x = 2;
        int y = 80;
        if (recipe.getMinHunterLevel() > 1) {
            String level = UtilLib.translateFormatted("gui.vampirism.hunter_weapon_table.level", recipe.getMinHunterLevel());

            minecraft.fontRendererObj.drawString(level, x, y, Color.gray.getRGB());
            y += minecraft.fontRendererObj.FONT_HEIGHT + 2;
        }
        if (recipe.getRequiredSkills().length > 0) {
            String skills = "";
            for (ISkill<IHunterPlayer> skill : recipe.getRequiredSkills()) {
                skills += UtilLib.translate(skill.getUnlocalizedName()) + " ";

            }
            String skillText = UtilLib.translateFormatted("gui.vampirism.hunter_weapon_table.skill", skills);
            minecraft.fontRendererObj.drawSplitString(skillText, x, y, 132, Color.gray.getRGB());


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

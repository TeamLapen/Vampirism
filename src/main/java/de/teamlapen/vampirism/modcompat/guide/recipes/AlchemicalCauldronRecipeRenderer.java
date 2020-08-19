package de.teamlapen.vampirism.modcompat.guide.recipes;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.maxanier.guideapi.api.IRecipeRenderer;
import de.maxanier.guideapi.api.SubTexture;
import de.maxanier.guideapi.api.impl.Book;
import de.maxanier.guideapi.api.impl.abstraction.CategoryAbstract;
import de.maxanier.guideapi.api.impl.abstraction.EntryAbstract;
import de.maxanier.guideapi.api.util.GuiHelper;
import de.maxanier.guideapi.api.util.IngredientCycler;
import de.maxanier.guideapi.gui.BaseScreen;
import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.inventory.recipes.AlchemicalCauldronRecipe;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.TranslationTextComponent;

import java.awt.*;


public class AlchemicalCauldronRecipeRenderer extends IRecipeRenderer.RecipeRendererBase<AlchemicalCauldronRecipe> {

    private final SubTexture CRAFTING_GRID = new SubTexture(new ResourceLocation("vampirismguide", "textures/gui/alchemical_cauldron_recipe.png"), 0, 0, 110, 75);

    public AlchemicalCauldronRecipeRenderer(AlchemicalCauldronRecipe recipe) {
        super(recipe);
    }

    @Override
    public void draw(MatrixStack stack, Book book, CategoryAbstract categoryAbstract, EntryAbstract entryAbstract, int guiLeft, int guiTop, int mouseX, int mouseY, BaseScreen baseScreen, FontRenderer fontRenderer, IngredientCycler ingredientCycler) {

        CRAFTING_GRID.draw(stack, guiLeft + 60, guiTop + 42);
        AbstractGui.drawCenteredString(stack, fontRenderer, UtilLib.translate(ModBlocks.alchemical_cauldron.getTranslationKey()), guiLeft + baseScreen.xSize / 2, guiTop + 12, 0);
        AbstractGui.drawCenteredString(stack, fontRenderer, "§o" + UtilLib.translate("guideapi.text.crafting.shaped") + "§r", guiLeft + baseScreen.xSize / 2, guiTop + 14 + fontRenderer.FONT_HEIGHT, 0);

        int outputX = guiLeft + 150;
        int outputY = guiTop + 72;
        int in1X = guiLeft + 78;
        int in1Y = guiTop + 59;
        int in2X = guiLeft + 108;
        int in2Y = guiTop + 59;

        ItemStack itemStack = recipe.getRecipeOutput();


        GuiHelper.drawItemStack(stack, itemStack, outputX, outputY);
        if (GuiHelper.isMouseBetween(mouseX, mouseY, outputX, outputY, 15, 15)) {
            tooltips = GuiHelper.getTooltip(itemStack);
        }

        Ingredient input = recipe.getIngredients().get(0);
        ingredientCycler.getCycledIngredientStack(input, 0).ifPresent(inStack -> {

            GuiHelper.drawItemStack(stack, inStack, in2X, in2Y);
            if (GuiHelper.isMouseBetween(mouseX, mouseY, in2X, in2Y, 15, 15)) {
                tooltips = GuiHelper.getTooltip(inStack);
            }
        });


        Ingredient liquid = recipe.getFluid().map(ingredient -> ingredient, fluidStack -> Ingredient.fromItems(fluidStack.getFluid().getFilledBucket()));

        ingredientCycler.getCycledIngredientStack(liquid, 1).ifPresent(fluidStack -> {
            GuiHelper.drawItemStack(stack, fluidStack, in1X, in1Y);
            if (GuiHelper.isMouseBetween(mouseX, mouseY, in1X, in1Y, 15, 15)) {
                tooltips = GuiHelper.getTooltip(fluidStack);
            }
        });


        int y = guiTop + 120;
        if (recipe.getRequiredLevel() > 1) {
            ITextComponent level = new TranslationTextComponent("gui.vampirism.hunter_weapon_table.level", recipe.getRequiredLevel());
            fontRenderer.func_243248_b(stack, level, guiLeft + 50, y, Color.gray.getRGB());
            y += fontRenderer.FONT_HEIGHT + 2;
        }
        if (recipe.getRequiredSkills().length > 0) {
            StringBuilder skills = new StringBuilder();
            for (ISkill skill : recipe.getRequiredSkills()) {
                skills.append("\n§o").append(UtilLib.translate(skill.getTranslationKey())).append("§r ");

            }
            ITextProperties skillText = new TranslationTextComponent("gui.vampirism.hunter_weapon_table.skill", skills.toString());
            fontRenderer.func_238418_a_(skillText, guiLeft + 50, y, 100, Color.gray.getRGB());
        }
    }


}

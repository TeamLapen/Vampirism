package de.teamlapen.vampirism.modcompat.guide.recipes;

import amerifrance.guideapi.api.IRecipeRenderer;
import amerifrance.guideapi.api.SubTexture;
import amerifrance.guideapi.api.impl.Book;
import amerifrance.guideapi.api.impl.abstraction.CategoryAbstract;
import amerifrance.guideapi.api.impl.abstraction.EntryAbstract;
import amerifrance.guideapi.api.util.GuiHelper;
import amerifrance.guideapi.api.util.IngredientCycler;
import amerifrance.guideapi.gui.BaseScreen;
import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.items.IWeaponTableRecipe;
import de.teamlapen.vampirism.core.ModBlocks;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.awt.*;


public class BasicWeaponTableRecipeRenderer<T extends IWeaponTableRecipe> extends IRecipeRenderer.RecipeRendererBase<T> {

    private final SubTexture CRAFTING_GRID = new SubTexture(new ResourceLocation("vampirismguide", "textures/gui/weapon_table_recipe.png"), 0, 0, 110, 75);

    public BasicWeaponTableRecipeRenderer(T recipe) {
        super(recipe);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void draw(Book book, CategoryAbstract categoryAbstract, EntryAbstract entryAbstract, int guiLeft, int guiTop, int mouseX, int mouseY, BaseScreen baseScreen, FontRenderer fontRenderer, IngredientCycler ingredientCycler) {


        CRAFTING_GRID.draw(guiLeft + 42, guiTop + 43);

        baseScreen.drawCenteredString(fontRenderer, UtilLib.translate(ModBlocks.weapon_table.getTranslationKey()), guiLeft + baseScreen.xSize / 2, guiTop + 12, 0);
        baseScreen.drawCenteredString(fontRenderer, "§o" + getRecipeName() + "§r", guiLeft + baseScreen.xSize / 2, guiTop + 14 + fontRenderer.FONT_HEIGHT, 0);

        int outputX = 3 + (6 * 17) + (guiLeft + baseScreen.xSize / 7);
        int outputY = (2 * 17) + (guiTop + baseScreen.xSize / 5);

        ItemStack stack = recipe.getRecipeOutput();


        GuiHelper.drawItemStack(stack, outputX, outputY);
        if (GuiHelper.isMouseBetween(mouseX, mouseY, outputX, outputY, 15, 15)) {
            tooltips = GuiHelper.getTooltip(recipe.getRecipeOutput());
        }

        if (recipe.getRequiredLavaUnits() > 0) {
            GuiHelper.drawItemStack(new ItemStack(Items.LAVA_BUCKET), outputX - 16, outputY + 21);
        }

        int y = guiTop + 120;
        if (recipe.getRequiredLevel() > 1) {
            String level = UtilLib.translate("gui.vampirism.hunter_weapon_table.level", recipe.getRequiredLevel());
            baseScreen.drawString(fontRenderer, level, guiLeft + 40, y, Color.gray.getRGB());
            y += fontRenderer.FONT_HEIGHT + 2;
        }
        if (recipe.getRequiredSkills() != null && recipe.getRequiredSkills().length > 0) {
            StringBuilder skills = new StringBuilder();
            for (ISkill skill : recipe.getRequiredSkills()) {
                skills.append("\n§o").append(UtilLib.translate(skill.getTranslationKey())).append("§r ");

            }
            String skillText = UtilLib.translate("gui.vampirism.hunter_weapon_table.skill", skills.toString());
            fontRenderer.drawSplitString(skillText, guiLeft + 40, y, 110, Color.gray.getRGB());
        }
    }

    protected String getRecipeName() {
        return UtilLib.translate("guideapi.text.crafting.shaped");
    }


}

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
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.items.IWeaponTableRecipe;
import de.teamlapen.vampirism.core.ModBlocks;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;


public class BasicWeaponTableRecipeRenderer<T extends IWeaponTableRecipe> extends IRecipeRenderer.RecipeRendererBase<T> {

    private final SubTexture CRAFTING_GRID = new SubTexture(new ResourceLocation("vampirismguide", "textures/gui/weapon_table_recipe.png"), 0, 0, 110, 75);

    public BasicWeaponTableRecipeRenderer(T recipe) {
        super(recipe);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void draw(MatrixStack stack, Book book, CategoryAbstract categoryAbstract, EntryAbstract entryAbstract, int guiLeft, int guiTop, int mouseX, int mouseY, BaseScreen baseScreen, FontRenderer fontRenderer, IngredientCycler ingredientCycler) {


        CRAFTING_GRID.draw(stack, guiLeft + 62, guiTop + 43);
        baseScreen.drawCenteredStringWithoutShadow(stack, fontRenderer, ModBlocks.WEAPON_TABLE.get().getName(), guiLeft + baseScreen.xSize / 2, guiTop + 12, 0);
        baseScreen.drawCenteredStringWithoutShadow(stack, fontRenderer, getRecipeName().withStyle(style -> style.withItalic(true)), guiLeft + baseScreen.xSize / 2, guiTop + 14 + fontRenderer.lineHeight, 0);

        int outputX = guiLeft + 152;
        int outputY = guiTop + 72;

        ItemStack itemStack = recipe.getResultItem();


        GuiHelper.drawItemStack(stack, itemStack, outputX, outputY);
        if (GuiHelper.isMouseBetween(mouseX, mouseY, outputX, outputY, 15, 15)) {
            tooltips = GuiHelper.getTooltip(recipe.getResultItem());
        }

        if (recipe.getRequiredLavaUnits() > 0) {
            GuiHelper.drawItemStack(stack, new ItemStack(Items.LAVA_BUCKET), outputX - 16, outputY + 21);
        }

        int y = guiTop + 120;
        if (recipe.getRequiredLevel() > 1) {
            ITextComponent level = new TranslationTextComponent("gui.vampirism.hunter_weapon_table.level", recipe.getRequiredLevel());
            fontRenderer.draw(stack, level, guiLeft + 40, y, Color.gray.getRGB());
            y += fontRenderer.lineHeight + 2;
        }
        if (recipe.getRequiredSkills().length > 0) {
            ITextProperties newLine = new StringTextComponent("\n");
            List<ITextProperties> skills = new ArrayList<>();
            skills.add(new TranslationTextComponent("gui.vampirism.hunter_weapon_table.skill", "\n"));
            for (ISkill skill : recipe.getRequiredSkills()) {
                skills.add(skill.getName().copy().withStyle(TextFormatting.ITALIC));
                skills.add(newLine);
            }
            fontRenderer.drawWordWrap(ITextProperties.composite(skills), guiLeft + 40, y, 110, Color.gray.getRGB());
        }
    }

    protected IFormattableTextComponent getRecipeName() {
        return new TranslationTextComponent("guideapi.text.crafting.shaped");
    }


}

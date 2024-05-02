package de.teamlapen.vampirism.modcompat.guide.recipes;

import de.maxanier.guideapi.api.IRecipeRenderer;
import de.maxanier.guideapi.api.SubTexture;
import de.maxanier.guideapi.api.impl.Book;
import de.maxanier.guideapi.api.impl.abstraction.CategoryAbstract;
import de.maxanier.guideapi.api.impl.abstraction.EntryAbstract;
import de.maxanier.guideapi.api.util.GuiHelper;
import de.maxanier.guideapi.api.util.IngredientCycler;
import de.maxanier.guideapi.gui.BaseScreen;
import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.lib.util.Color;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.recipes.AlchemicalCauldronRecipe;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class AlchemicalCauldronRecipeRenderer extends IRecipeRenderer.RecipeRendererBase<AlchemicalCauldronRecipe> {

    private final SubTexture CRAFTING_GRID = new SubTexture(new ResourceLocation("vampirismguide", "textures/gui/alchemical_cauldron_recipe.png"), 0, 0, 110, 75);

    public AlchemicalCauldronRecipeRenderer(AlchemicalCauldronRecipe recipe) {
        super(recipe);
    }

    @Override
    public void draw(@NotNull GuiGraphics guiGraphics, RegistryAccess registryAccess, Book book, CategoryAbstract categoryAbstract, EntryAbstract entryAbstract, int guiLeft, int guiTop, int mouseX, int mouseY, @NotNull BaseScreen baseScreen, @NotNull Font font, @NotNull IngredientCycler ingredientCycler) {

        CRAFTING_GRID.draw(guiGraphics, guiLeft + 60, guiTop + 42);
        baseScreen.drawCenteredStringWithoutShadow(guiGraphics, font, UtilLib.translate(ModBlocks.ALCHEMICAL_CAULDRON.get().getDescriptionId()), guiLeft + baseScreen.xSize / 2, guiTop + 12, 0);
        baseScreen.drawCenteredStringWithoutShadow(guiGraphics, font, "§o" + UtilLib.translate("guideapi.text.crafting.shaped") + "§r", guiLeft + baseScreen.xSize / 2, guiTop + 14 + font.lineHeight, 0);

        int outputX = guiLeft + 150;
        int outputY = guiTop + 72;
        int in1X = guiLeft + 78;
        int in1Y = guiTop + 59;
        int in2X = guiLeft + 108;
        int in2Y = guiTop + 59;

        ItemStack itemStack = recipe.getResultItem(registryAccess);


        GuiHelper.drawItemStack(guiGraphics, itemStack, outputX, outputY);
        if (GuiHelper.isMouseBetween(mouseX, mouseY, outputX, outputY, 15, 15)) {
            tooltips = GuiHelper.getTooltip(itemStack);
        }

        Ingredient input = recipe.getIngredients().get(0);
        ingredientCycler.getCycledIngredientStack(input, 0).ifPresent(inStack -> {

            GuiHelper.drawItemStack(guiGraphics, inStack, in2X, in2Y);
            if (GuiHelper.isMouseBetween(mouseX, mouseY, in2X, in2Y, 15, 15)) {
                tooltips = GuiHelper.getTooltip(inStack);
            }
        });


        Ingredient liquid = recipe.getFluid().map(ingredient -> ingredient, fluidStack -> Ingredient.of(fluidStack.getFluid().getBucket()));

        ingredientCycler.getCycledIngredientStack(liquid, 1).ifPresent(fluidStack -> {
            GuiHelper.drawItemStack(guiGraphics, fluidStack, in1X, in1Y);
            if (GuiHelper.isMouseBetween(mouseX, mouseY, in1X, in1Y, 15, 15)) {
                tooltips = GuiHelper.getTooltip(fluidStack);
            }
        });


        int y = guiTop + 120;
        if (recipe.getRequiredLevel() > 1) {
            Component level = Component.translatable("gui.vampirism.hunter_weapon_table.level", recipe.getRequiredLevel());
            guiGraphics.drawString(font, level, guiLeft + 50, y, Color.GRAY.getRGB(), false);
            y += font.lineHeight + 2;
        }
        if (!recipe.getRequiredSkills().isEmpty()) {
            FormattedText newLine = Component.literal("\n");
            List<FormattedText> skills = new ArrayList<>();
            skills.add(Component.translatable("gui.vampirism.skill_required", "\n"));
            for (ISkill<?> skill : recipe.getRequiredSkills()) {
                skills.add(skill.getName().copy().withStyle(ChatFormatting.ITALIC));
                skills.add(newLine);
            }
            guiGraphics.drawWordWrap(font, FormattedText.composite(skills), guiLeft + 50, y, 100, Color.GRAY.getRGB());
        }
    }


}

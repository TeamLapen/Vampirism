package de.teamlapen.vampirism.common.integration.guide.recipes;

import de.maxanier.guideapi.api.GuideBookScreen;
import de.maxanier.guideapi.api.book.Book;
import de.maxanier.guideapi.api.category.CategoryBase;
import de.maxanier.guideapi.api.entry.EntryBase;
import de.maxanier.guideapi.api.recipes.IRecipeRenderer;
import de.maxanier.guideapi.api.util.GuiHelper;
import de.maxanier.guideapi.api.util.IngredientCycler;
import de.maxanier.guideapi.api.util.SubTexture;
import de.teamlapen.faction.api.factions.skills.ISkill;
import de.teamlapen.faction.common.util.Color;
import de.teamlapen.vampirism.common.core.ModBlocks;
import de.teamlapen.vampirism.common.world.items.recipes.AlchemicalCauldronRecipe;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class AlchemicalCauldronRecipeRenderer extends IRecipeRenderer.RecipeRendererBase<AlchemicalCauldronRecipe> {

    private final SubTexture CRAFTING_GRID = new SubTexture(Identifier.fromNamespaceAndPath("vampirismguide", "textures/gui/alchemical_cauldron_recipe.png"), 0, 0, 110, 75);

    public AlchemicalCauldronRecipeRenderer(RecipeHolder<AlchemicalCauldronRecipe> recipe) {
        super(recipe);
    }

    @Override
    public void draw(GuiGraphics guiGraphics, Book book, CategoryBase category, EntryBase entry, int guiLeft, int guiTop, int mouseX, int mouseY, GuideBookScreen screen, Font font, IngredientCycler ingredientCycler){
        CRAFTING_GRID.draw(guiGraphics, guiLeft + 60, guiTop + 42);
        GuiHelper.drawCenteredStringWithoutShadow(guiGraphics, font, ModBlocks.ALCHEMICAL_CAULDRON.get().getName(), guiLeft + screen.xSize() / 2, guiTop + 12, book.getTextColor());
        GuiHelper.drawCenteredStringWithoutShadow(guiGraphics, font, Component.literal("§o").append(Component.translatable("guideapi.text.crafting.shaped")).append("§r"), guiLeft + screen.xSize() / 2, guiTop + 14 + font.lineHeight, book.getTextColor());

        int outputX = guiLeft + 150;
        int outputY = guiTop + 71;
        int in1X = guiLeft + 78;
        int in1Y = guiTop + 59;
        int in2X = guiLeft + 108;
        int in2Y = guiTop + 59;

        ItemStack itemStack = recipe.value().result();


        GuiHelper.drawItemStack(guiGraphics, itemStack, outputX, outputY);
        if (GuiHelper.isMouseBetween(mouseX, mouseY, outputX, outputY, 15, 15)) {
            tooltips = GuiHelper.getTooltip(itemStack);
        }

        Ingredient input = recipe.value().getIngredient();
        ingredientCycler.getCycledIngredientStack(input, 0).ifPresent(inStack -> {

            GuiHelper.drawItemStack(guiGraphics, inStack, in2X, in2Y);
            if (GuiHelper.isMouseBetween(mouseX, mouseY, in2X, in2Y, 15, 15)) {
                tooltips = GuiHelper.getTooltip(inStack);
            }
        });


        Ingredient liquid = recipe.value().getFluid().map(ingredient -> ingredient, fluidStack -> Ingredient.of(fluidStack.getFluid().getBucket()));

        ingredientCycler.getCycledIngredientStack(liquid, 1).ifPresent(fluidStack -> {
            GuiHelper.drawItemStack(guiGraphics, fluidStack, in1X, in1Y);
            if (GuiHelper.isMouseBetween(mouseX, mouseY, in1X, in1Y, 15, 15)) {
                tooltips = GuiHelper.getTooltip(fluidStack);
            }
        });


        int y = guiTop + 120;
        if (recipe.value().getRequiredLevel() > 1) {
            Component level = Component.translatable("container.vampirism.hunter_table.level", recipe.value().getRequiredLevel());
            guiGraphics.drawString(font, level, guiLeft + 50, y, Color.GRAY.getRGB(), false);
            y += font.lineHeight + 2;
        }
        if (!recipe.value().getRequiredSkills().isEmpty()) {
            FormattedText newLine = Component.literal("\n");
            List<FormattedText> skills = new ArrayList<>();
            skills.add(Component.translatable("gui.vampirism.skill_required", "\n"));
            for (Holder<ISkill<?>> skill : recipe.value().getRequiredSkills()) {
                skills.add(skill.value().getName().copy().withStyle(ChatFormatting.ITALIC));
                skills.add(newLine);
            }
            guiGraphics.drawWordWrap(font, FormattedText.composite(skills), guiLeft + 50, y, 100, Color.GRAY.getRGB(),false);
        }
    }


}

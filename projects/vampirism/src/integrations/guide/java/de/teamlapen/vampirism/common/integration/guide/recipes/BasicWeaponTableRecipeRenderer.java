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
import de.teamlapen.vampirism.api.world.items.IWeaponTableRecipe;
import de.teamlapen.vampirism.common.core.ModBlocks;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class BasicWeaponTableRecipeRenderer<T extends IWeaponTableRecipe, Q extends RecipeDisplay> extends IRecipeRenderer.RecipeDisplayRenderer<T, Q> {

    private final SubTexture CRAFTING_GRID = new SubTexture(Identifier.fromNamespaceAndPath("vampirismguide", "textures/gui/weapon_table_recipe.png"), 0, 0, 110, 75);

    public BasicWeaponTableRecipeRenderer(RecipeHolder<T> recipe, Class<Q> recipeDisplayClass) {
        super(recipe, recipeDisplayClass);
    }

    @Override
    public void draw(@NotNull GuiGraphics guiGraphics, Book book, CategoryBase categoryAbstract, EntryBase entryAbstract, int guiLeft, int guiTop, int mouseX, int mouseY, @NotNull GuideBookScreen baseScreen, @NotNull Font fontRenderer, IngredientCycler ingredientCycler) {


        CRAFTING_GRID.draw(guiGraphics, guiLeft + 62, guiTop + 43);
        GuiHelper.drawCenteredStringWithoutShadow(guiGraphics, fontRenderer, ModBlocks.WEAPON_TABLE.get().getName(), guiLeft + baseScreen.xSize() / 2, guiTop + 12, book.getTextColor());
        GuiHelper.drawCenteredStringWithoutShadow(guiGraphics, fontRenderer, getRecipeName().withStyle(style -> style.withItalic(true)), guiLeft + baseScreen.xSize() / 2, guiTop + 14 + fontRenderer.lineHeight, book.getTextColor());

        int outputX = guiLeft + 152;
        int outputY = guiTop + 72;


        ItemStack outputStack = ingredientCycler.getCycledIngredientStack(this.outputs, -1);

        GuiHelper.drawItemStack(guiGraphics, outputStack, outputX, outputY);
        if (GuiHelper.isMouseBetween(mouseX, mouseY, outputX, outputY, 15, 15)) {
            tooltips = GuiHelper.getTooltip(outputStack);
        }

        if (recipe.value().getRequiredLavaUnits() > 0) {
            GuiHelper.drawItemStack(guiGraphics, new ItemStack(Items.LAVA_BUCKET), outputX - 16, outputY + 21);
        }

        int y = guiTop + 120;
        if (recipe.value().getRequiredLevel() > 1) {
            Component level = Component.translatable("container.vampirism.hunter_table.level", recipe.value().getRequiredLevel());
            guiGraphics.drawString(fontRenderer, level, guiLeft + 40, y, Color.GRAY.getRGB(), false);
            y += fontRenderer.lineHeight + 2;
        }
        if (!recipe.value().getRequiredSkills().isEmpty()) {
            FormattedText newLine = Component.literal("\n");
            List<FormattedText> skills = new ArrayList<>();
            skills.add(Component.translatable("gui.vampirism.skill_required", "\n"));
            for (Holder<ISkill<?>> skill : recipe.value().getRequiredSkills()) {
                skills.add(skill.value().getName().copy().withStyle(ChatFormatting.ITALIC));
                skills.add(newLine);
            }
            guiGraphics.drawWordWrap(fontRenderer, FormattedText.composite(skills), guiLeft + 40, y, 110, Color.GRAY.getRGB());
        }
    }

    protected MutableComponent getRecipeName() {
        return Component.translatable("guideapi.text.crafting.shaped");
    }


}

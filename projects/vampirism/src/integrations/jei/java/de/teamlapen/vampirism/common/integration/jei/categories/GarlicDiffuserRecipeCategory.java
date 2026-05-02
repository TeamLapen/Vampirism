package de.teamlapen.vampirism.common.integration.jei.categories;

import de.teamlapen.vampirism.common.core.ModBlocks;
import de.teamlapen.vampirism.common.integration.jei.VampirismJEIPlugin;
import de.teamlapen.vampirism.common.integration.jei.recipes.GarlicDiffuserRecipe;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class GarlicDiffuserRecipeCategory implements IRecipeCategory<GarlicDiffuserRecipe> {

    public static final int width = 150;
    public static final int height = 18;

    private final IDrawable background;
    private final IDrawable slot;
    private final IDrawable icon;
    private final Component localizedName;

    public GarlicDiffuserRecipeCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createBlankDrawable(width, height);
        this.slot = guiHelper.getSlotDrawable();
        this.icon = guiHelper.createDrawableItemStack(ModBlocks.GARLIC_DIFFUSER_NORMAL.toStack());
        this.localizedName = Component.translatable("block.vampirism.garlic_diffuser_normal");
    }

    @Override
    public @NotNull IRecipeType<GarlicDiffuserRecipe> getRecipeType() {
        return VampirismJEIPlugin.GARLIC_DIFFUSER;
    }

    @Override
    public @NotNull Component getTitle() {
        return this.localizedName;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public @NotNull IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, GarlicDiffuserRecipe recipe, @NotNull IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 1, 1)
                .addItemStacks(recipe.getInputs());
    }

    @Override
    public void draw(GarlicDiffuserRecipe recipe, @NotNull IRecipeSlotsView recipeSlotsView, @NotNull GuiGraphicsExtractor GuiGraphicsExtractor, double mouseX, double mouseY) {
        this.background.draw(GuiGraphicsExtractor);
        GuiGraphicsExtractor.pose().pushMatrix();
        this.slot.draw(GuiGraphicsExtractor);

        int burnDuration = recipe.getBurnTime() / 20;
        Component text;
        if (burnDuration < 60) {
            text = Component.translatable("gui.vampirism.jei.category.diffuser.burn_duration_seconds", burnDuration);
        } else if (burnDuration < 3600) {
            text = Component.translatable("gui.vampirism.jei.category.diffuser.burn_duration_minutes", burnDuration / 60, (burnDuration % 60));
        } else {
            text = Component.translatable("gui.vampirism.jei.category.diffuser.burn_duration_hours", burnDuration / 3600, (burnDuration % 3600) / 60, (burnDuration % 3600 % 60));
        }

        GuiGraphicsExtractor.drawString(Minecraft.getInstance().font, text, 24, 5, 0xFF808080, false);
        GuiGraphicsExtractor.pose().popMatrix();
    }
}

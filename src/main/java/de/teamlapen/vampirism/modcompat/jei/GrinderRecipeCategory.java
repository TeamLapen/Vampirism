package de.teamlapen.vampirism.modcompat.jei;

import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.modcompat.jei.recipes.GrinderRecipe;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.jetbrains.annotations.NotNull;

public class GrinderRecipeCategory implements IRecipeCategory<GrinderRecipe> {

    public static final int width = 150;
    public static final int height = 18;

    private final IDrawable background;
    private final IDrawable slot;
    private final IDrawable icon;
    private final Component localizedName;

    public GrinderRecipeCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createBlankDrawable(width, height);
        this.slot = guiHelper.getSlotDrawable();
        this.icon = guiHelper.createDrawableItemStack(ModBlocks.BLOOD_GRINDER.toStack());
        this.localizedName = Component.translatable("block.vampirism.blood_grinder");
    }

    @Override
    public @NotNull RecipeType<GrinderRecipe> getRecipeType() {
        return VampirismJEIPlugin.GRINDER_RECIPE;
    }

    @Override
    public @NotNull Component getTitle() {
        return this.localizedName;
    }

    @Override
    public @NotNull IDrawable getBackground() {
        return this.background;
    }

    @Override
    public @NotNull IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, GrinderRecipe recipe, @NotNull IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 1,  1)
                .addItemStack(recipe.input());
    }

    @Override
    public void draw(GrinderRecipe recipe, @NotNull IRecipeSlotsView recipeSlotsView, @NotNull GuiGraphics guiGraphics, double mouseX, double mouseY) {
        this.slot.draw(guiGraphics);

        int blood = recipe.itemBlood().blood();

        MutableComponent text = Component.translatable("gui.vampirism.jei.category.grinder.blood", blood);

        guiGraphics.drawString(Minecraft.getInstance().font, text, 24, 5, 0xFF808080, false);
    }
}

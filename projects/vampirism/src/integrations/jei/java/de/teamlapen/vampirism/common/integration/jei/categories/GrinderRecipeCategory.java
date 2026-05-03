package de.teamlapen.vampirism.common.integration.jei.categories;

import de.teamlapen.vampirism.common.core.ModBlocks;
import de.teamlapen.vampirism.common.core.ModFluids;
import de.teamlapen.vampirism.common.integration.jei.VampirismJEIPlugin;
import de.teamlapen.vampirism.common.integration.jei.recipes.GrinderRecipe;
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
import net.minecraft.network.chat.MutableComponent;

public class GrinderRecipeCategory implements IRecipeCategory<GrinderRecipe> {

    public static final int width = 166;
    public static final int height = 18;

    private final IDrawable background;
    private final IDrawable inputSlot;
    private final IDrawable outputSlot;
    private final IDrawable icon;
    private final Component localizedName;

    public GrinderRecipeCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createBlankDrawable(width, height);
        this.inputSlot = guiHelper.getSlotDrawable();
        this.outputSlot = guiHelper.getSlotDrawable();
        this.icon = guiHelper.createDrawableItemStack(ModBlocks.BLOOD_GRINDER.toStack());
        this.localizedName = Component.translatable("block.vampirism.blood_grinder");
    }

    @Override
    public IRecipeType<GrinderRecipe> getRecipeType() {
        return VampirismJEIPlugin.GRINDER_RECIPE;
    }

    @Override
    public Component getTitle() {
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
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, GrinderRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 1, 1).add(recipe.input());
        builder.addSlot(RecipeIngredientRole.OUTPUT, 149, 1).add(ModFluids.BLOOD.get(), recipe.blood());
    }

    @Override
    public void draw(GrinderRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphicsExtractor GuiGraphicsExtractor, double mouseX, double mouseY) {
        this.background.draw(GuiGraphicsExtractor);
        GuiGraphicsExtractor.pose().pushMatrix();
        this.inputSlot.draw(GuiGraphicsExtractor);
        this.outputSlot.draw(GuiGraphicsExtractor, 148, 0);

        int blood = recipe.itemBlood().blood();

        MutableComponent text = Component.translatable("gui.vampirism.jei.category.grinder.blood", blood);

        GuiGraphicsExtractor.text(Minecraft.getInstance().font, text, 24, 5, 0xFF808080, false);
        GuiGraphicsExtractor.pose().popMatrix();
    }
}

package de.teamlapen.vampirism.modcompat.jei.categories;

import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.modcompat.jei.VampirismJEIPlugin;
import de.teamlapen.vampirism.modcompat.jei.recipes.FogDiffuserRecipe;
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
import org.jetbrains.annotations.NotNull;

public class FogDiffuserRecipeCategory implements IRecipeCategory<FogDiffuserRecipe> {

    public static final int width = 150;
    public static final int height = 18;

    private final IDrawable background;
    private final IDrawable slot;
    private final IDrawable icon;
    private final Component localizedName;

    public FogDiffuserRecipeCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createBlankDrawable(width, height);
        this.slot = guiHelper.getSlotDrawable();
        this.icon = guiHelper.createDrawableItemStack(ModBlocks.FOG_DIFFUSER.toStack());
        this.localizedName = Component.translatable("block.vampirism.fog_diffuser");
    }

    @Override
    public @NotNull RecipeType<FogDiffuserRecipe> getRecipeType() {
        return VampirismJEIPlugin.FOG_DIFFUSER;
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
    public void setRecipe(IRecipeLayoutBuilder builder, FogDiffuserRecipe recipe, @NotNull IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 1,  1)
                .addItemStacks(recipe.getInputs());
    }

    @Override
    public void draw(FogDiffuserRecipe recipe, @NotNull IRecipeSlotsView recipeSlotsView, @NotNull GuiGraphics guiGraphics, double mouseX, double mouseY) {
        this.slot.draw(guiGraphics);
        
        int burnDuration = recipe.getBurnTime() / 20;
        Component text;
        if (burnDuration < 60) {
            text = Component.translatable("gui.vampirism.jei.category.diffuser.burn_duration_seconds", burnDuration);
        } else if (burnDuration < 3600) {
            text = Component.translatable("gui.vampirism.jei.category.diffuser.burn_duration_minutes", burnDuration / 60, burnDuration % 60);
        } else {
            text = Component.translatable("gui.vampirism.jei.category.diffuser.burn_duration_hours", burnDuration / 3600, burnDuration % 3600, burnDuration % 3600 % 60);
        }

        guiGraphics.drawString(Minecraft.getInstance().font, text, 24, 5, 0xFF808080, false);
    }
}

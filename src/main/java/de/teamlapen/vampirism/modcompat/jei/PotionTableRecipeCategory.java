package de.teamlapen.vampirism.modcompat.jei;

import de.teamlapen.lib.util.Color;
import de.teamlapen.vampirism.client.gui.screens.PotionTableScreen;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.entity.player.hunter.skills.HunterSkills;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.ITickTimer;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;


public class PotionTableRecipeCategory implements IRecipeCategory<JEIPotionMix> {

    private final @NotNull Component localizedName;
    private final @NotNull IDrawable background;
    private final @NotNull IDrawable icon;
    private final @NotNull IDrawable slotDrawable;
    private final @NotNull IDrawable arrow;
    private final @NotNull IDrawable bubbles;
    private final @NotNull IDrawable blazeHeat;


    PotionTableRecipeCategory(@NotNull IGuiHelper guiHelper) {
        this.localizedName = Component.translatable(ModBlocks.POTION_TABLE.get().getDescriptionId());
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.POTION_TABLE.get()));
        this.background = guiHelper.drawableBuilder(PotionTableScreen.BACKGROUND, 65, 6, 103, 73).addPadding(0, 33, 0, 25).build();
        this.slotDrawable = guiHelper.getSlotDrawable();

        var bubbles = guiHelper.drawableBuilder(fixSprite(PotionTableScreen.BUBBLES_SPRITE), 0, 0, 12, 29).setTextureSize(12, 29).build();
        this.bubbles = guiHelper.createAnimatedDrawable(bubbles, 400, IDrawableAnimated.StartDirection.BOTTOM, false);

        var blaze = guiHelper.drawableBuilder(fixSprite(PotionTableScreen.FUEL_SPRITE), 0, 0, 18, 4).setTextureSize(18, 4).build();
        this.blazeHeat = guiHelper.createAnimatedDrawable(blaze, 400, IDrawableAnimated.StartDirection.LEFT, false);

        var progress = guiHelper.drawableBuilder(fixSprite(PotionTableScreen.PROGRESS_SPRITE), 0, 0, 9, 28).setTextureSize(9, 28).build();
        this.arrow = guiHelper.createAnimatedDrawable(progress, 400, IDrawableAnimated.StartDirection.TOP, false);
    }

    private static ResourceLocation fixSprite(ResourceLocation spriteLoc) {
        return spriteLoc.withPrefix("textures/gui/sprites/").withSuffix(".png");
    }

    @Override
    public void draw(@NotNull JEIPotionMix recipe, @NotNull IRecipeSlotsView recipeSlotsView, @NotNull GuiGraphics graphics, double mouseX, double mouseY) {
        this.blazeHeat.draw(graphics, 1, 35);
        this.bubbles.draw(graphics, 3, 4);
        this.arrow.draw(graphics, 80, 10);

        int y = 75;
        int x = 5;
        Minecraft minecraft = Minecraft.getInstance();
        if (recipe.getOriginal().durable && recipe.getOriginal().concentrated) {
            graphics.drawString(minecraft.font, HunterSkills.CONCENTRATED_DURABLE_BREWING.get().getName(), x, y, Color.GRAY.getRGB(), false);
            y += minecraft.font.lineHeight;
        } else if (recipe.getOriginal().durable) {
            graphics.drawString(minecraft.font, HunterSkills.DURABLE_BREWING.get().getName(), x, y, Color.GRAY.getRGB(), false);
            y += minecraft.font.lineHeight;
        } else if (recipe.getOriginal().concentrated) {
            graphics.drawString(minecraft.font, HunterSkills.CONCENTRATED_BREWING.get().getName(), x, y, Color.GRAY.getRGB(), false);
            y += minecraft.font.lineHeight;
        }
        if (recipe.getOriginal().master) {
            graphics.drawString(minecraft.font, HunterSkills.MASTER_BREWER.get().getName(), x, y, Color.GRAY.getRGB(), false);
            y += minecraft.font.lineHeight;
        }
        if (recipe.getOriginal().efficient) {
            graphics.drawString(minecraft.font, HunterSkills.EFFICIENT_BREWING.get().getName(), x, y, Color.GRAY.getRGB(), false);
            y += minecraft.font.lineHeight;
        }
    }

    @NotNull
    @Override
    public IDrawable getBackground() {
        return background;
    }

    @NotNull
    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @NotNull
    @Override
    public Component getTitle() {
        return localizedName;
    }

    @Override
    public @NotNull RecipeType<JEIPotionMix> getRecipeType() {
        return VampirismJEIPlugin.POTION;
    }

    @Override
    public void setRecipe(@NotNull IRecipeLayoutBuilder builder, @NotNull JEIPotionMix recipe, @NotNull IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 39, 53).addItemStack(recipe.getPotionInput());
        builder.addSlot(RecipeIngredientRole.INPUT, 61, 53).addItemStack(recipe.getPotionInput());
        builder.addSlot(RecipeIngredientRole.INPUT, 83, 53).addItemStack(recipe.getPotionInput());
        builder.addSlot(RecipeIngredientRole.INPUT, 61, 2).addItemStacks(recipe.getMix1());
        builder.addSlot(RecipeIngredientRole.INPUT, 36, 10).addItemStacks(recipe.getMix2());
        builder.addSlot(RecipeIngredientRole.OUTPUT, 105, 15).addItemStack(recipe.getPotionOutput()).setBackground(this.slotDrawable, -1, -1);
    }

    private static class BrewingBubblesTickTimer implements ITickTimer {
        private static final int[] BUBBLE_LENGTHS = new int[] {29, 23, 18, 13, 9, 5, 0};
        private final @NotNull ITickTimer internalTimer;

        public BrewingBubblesTickTimer(@NotNull IGuiHelper guiHelper) {
            this.internalTimer = guiHelper.createTickTimer(14, BUBBLE_LENGTHS.length - 1, false);
        }

        public int getMaxValue() {
            return BUBBLE_LENGTHS[0];
        }

        public int getValue() {
            int timerValue = this.internalTimer.getValue();
            return BUBBLE_LENGTHS[timerValue];
        }
    }
}

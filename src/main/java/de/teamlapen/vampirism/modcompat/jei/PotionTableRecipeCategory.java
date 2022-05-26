package de.teamlapen.vampirism.modcompat.jei;

import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.lib.util.Color;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.player.hunter.skills.HunterSkills;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.ITickTimer;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;


public class PotionTableRecipeCategory implements IRecipeCategory<JEIPotionMix> {

    private static final ResourceLocation backgroundLocation = new ResourceLocation(REFERENCE.MODID, "textures/gui/potion_table.png");
    private final Component localizedName;
    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawable slotDrawable;
    private final IDrawableAnimated arrow;
    private final IDrawableAnimated bubbles;
    private final IDrawableStatic blazeHeat;


    PotionTableRecipeCategory(IGuiHelper guiHelper) {
        this.localizedName = new TranslatableComponent(ModBlocks.potion_table.get().getDescriptionId());
        this.icon = guiHelper.createDrawableIngredient(new ItemStack(ModBlocks.potion_table.get()));
        this.background = guiHelper.drawableBuilder(backgroundLocation, 65, 6, 103, 73).addPadding(0, 33, 0, 25).build();
        this.slotDrawable = guiHelper.getSlotDrawable();
        ITickTimer bubblesTickTimer = new BrewingBubblesTickTimer(guiHelper);
        this.bubbles = guiHelper.drawableBuilder(backgroundLocation, 185, 0, 12, 29).buildAnimated(bubblesTickTimer, IDrawableAnimated.StartDirection.BOTTOM);
        this.blazeHeat = guiHelper.createDrawable(backgroundLocation, 176, 29, 18, 4);
        this.arrow = guiHelper.drawableBuilder(backgroundLocation, 176, 0, 9, 28).buildAnimated(400, IDrawableAnimated.StartDirection.TOP, false);

    }

    @Override
    public void draw(JEIPotionMix recipe, @Nonnull PoseStack stack, double mouseX, double mouseY) {
        this.blazeHeat.draw(stack, 1, 35);
        this.bubbles.draw(stack, 3, 4);
        this.arrow.draw(stack, 80, 10);

        int y = 75;
        int x = 5;
        Minecraft minecraft = Minecraft.getInstance();
        if (recipe.getOriginal().durable && recipe.getOriginal().concentrated) {
            minecraft.font.draw(stack, HunterSkills.concentrated_durable_brewing.get().getName(), x, y, Color.GRAY.getRGB());
            y += minecraft.font.lineHeight;
        } else if (recipe.getOriginal().durable) {
            minecraft.font.draw(stack, HunterSkills.durable_brewing.get().getName(), x, y, Color.GRAY.getRGB());
            y += minecraft.font.lineHeight;
        } else if (recipe.getOriginal().concentrated) {
            minecraft.font.draw(stack, HunterSkills.concentrated_brewing.get().getName(), x, y, Color.GRAY.getRGB());
            y += minecraft.font.lineHeight;
        }
        if (recipe.getOriginal().master) {
            minecraft.font.draw(stack, HunterSkills.master_brewer.get().getName(), x, y, Color.GRAY.getRGB());
            y += minecraft.font.lineHeight;
        }
        if (recipe.getOriginal().efficient) {
            minecraft.font.draw(stack, HunterSkills.efficient_brewing.get().getName(), x, y, Color.GRAY.getRGB());
            y += minecraft.font.lineHeight;
        }
    }

    @Nonnull
    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Nonnull
    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Nonnull
    @Override
    public Class<? extends JEIPotionMix> getRecipeClass() {
        return JEIPotionMix.class;
    }

    @Nonnull
    @Override
    public Component getTitle() {
        return localizedName;
    }

    @Nonnull
    @Override
    public ResourceLocation getUid() {
        return VampirismJEIPlugin.POTION_RECIPE_UID;
    }

    @Override
    public void setIngredients(JEIPotionMix extendedPotionMix, IIngredients iIngredients) {

        iIngredients.setInputLists(VanillaTypes.ITEM, extendedPotionMix.getInputs());
        iIngredients.setOutput(VanillaTypes.ITEM, extendedPotionMix.getPotionOutput());
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, @Nonnull JEIPotionMix extendedPotionMix, @Nonnull IIngredients iIngredients) {
        IGuiItemStackGroup itemStacks = recipeLayout.getItemStacks();
        itemStacks.init(0, true, 38, 52);
        itemStacks.init(1, true, 60, 52);
        itemStacks.init(2, true, 82, 52);
        itemStacks.init(3, true, 60, 1);
        itemStacks.init(4, true, 35, 9);
        itemStacks.init(5, false, 103, 13);
        itemStacks.setBackground(5, this.slotDrawable);
        itemStacks.set(iIngredients);
    }

    private static class BrewingBubblesTickTimer implements ITickTimer {
        private static final int[] BUBBLE_LENGTHS = new int[]{29, 23, 18, 13, 9, 5, 0};
        private final ITickTimer internalTimer;

        public BrewingBubblesTickTimer(IGuiHelper guiHelper) {
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

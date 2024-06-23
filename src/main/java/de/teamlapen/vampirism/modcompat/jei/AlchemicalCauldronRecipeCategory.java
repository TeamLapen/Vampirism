package de.teamlapen.vampirism.modcompat.jei;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.lib.util.Color;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.client.gui.screens.AlchemicalCauldronScreen;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.recipes.AlchemicalCauldronRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableStatic;
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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.NotNull;


public class AlchemicalCauldronRecipeCategory implements IRecipeCategory<RecipeHolder<AlchemicalCauldronRecipe>> {
    private final @NotNull Component localizedName;
    private final @NotNull IDrawable background;
    private final @NotNull IDrawable icon;
    private final @NotNull IDrawableAnimated flame;
    private final @NotNull IDrawableAnimated arrow;
    private final @NotNull IDrawableAnimated bubbles;


    AlchemicalCauldronRecipeCategory(@NotNull IGuiHelper guiHelper) {
        this.localizedName = Component.translatable(ModBlocks.ALCHEMICAL_CAULDRON.get().getDescriptionId());
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.ALCHEMICAL_CAULDRON.get()));
        this.background = guiHelper.drawableBuilder(AlchemicalCauldronScreen.BACKGROUND, 38, 10, 120, 70).addPadding(0, 33, 0, 0).build();

        IDrawableStatic flameDrawable = guiHelper.drawableBuilder(fixSprite(AlchemicalCauldronScreen.LIT_PROGRESS_SPRITE), 0, 0, 14, 14).setTextureSize(14, 14).build();
        this.flame = guiHelper.createAnimatedDrawable(flameDrawable, 300, IDrawableAnimated.StartDirection.TOP, true);

        IDrawableStatic arrowDrawable = guiHelper.drawableBuilder(fixSprite(AlchemicalCauldronScreen.BURN_PROGRESS_SPRITE), 0, 0, 24, 16).setTextureSize(24, 16).build();
        this.arrow = guiHelper.createAnimatedDrawable(arrowDrawable, 200, IDrawableAnimated.StartDirection.LEFT, false);

        IDrawableStatic bubblesDrawable = guiHelper.drawableBuilder(fixSprite(AlchemicalCauldronScreen.BUBBLES_PROGRESS_SPRITE), 0, 0, 12, 29).setTextureSize(12, 29).build();
        this.bubbles = guiHelper.createAnimatedDrawable(bubblesDrawable, 200, IDrawableAnimated.StartDirection.BOTTOM, false);
    }

    private static ResourceLocation fixSprite(ResourceLocation spriteLoc) {
        return spriteLoc.withPrefix("textures/gui/sprites/").withSuffix(".png");
    }

    @Override
    public void draw(@NotNull RecipeHolder<AlchemicalCauldronRecipe> holder, @NotNull IRecipeSlotsView recipeSlotsView, @NotNull GuiGraphics graphics, double mouseX, double mouseY) {
        AlchemicalCauldronRecipe recipe = holder.value();
        this.flame.draw(graphics, 19, 27);
        this.arrow.draw(graphics, 41, 25);
        this.bubbles.draw(graphics, 104, 19);
        Minecraft minecraft = Minecraft.getInstance();
        int x = 0;
        int y = 65;
        if (recipe.getRequiredLevel() > 1) {
            Component level = Component.translatable("gui.vampirism.alchemical_cauldron.level", recipe.getRequiredLevel());
            graphics.drawString(minecraft.font, level, x, y, Color.GRAY.getRGB(), false);
            y += minecraft.font.lineHeight + 2;
        }
        if (!recipe.getRequiredSkills().isEmpty()) {
            MutableComponent skillText = Component.translatable("gui.vampirism.alchemical_cauldron.skill", " ");

            for (ISkill<?> s : recipe.getRequiredSkills()) {
                skillText.append(s.getName()).append(" ");
            }
            y += UtilLib.renderMultiLine(minecraft.font, graphics, skillText, 132, x, y, Color.GRAY.getRGB());
        }
    }


    @NotNull
    @Override
    public IDrawable getBackground() {
        return this.background;
    }

    @NotNull
    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public @NotNull RecipeType<RecipeHolder<AlchemicalCauldronRecipe>> getRecipeType() {
        return VampirismJEIPlugin.ALCHEMICAL_CAULDRON;
    }

    @NotNull
    @Override
    public Component getTitle() {
        return this.localizedName;
    }

    @Override
    public void setRecipe(@NotNull IRecipeLayoutBuilder builder, @NotNull RecipeHolder<AlchemicalCauldronRecipe> holder, @NotNull IFocusGroup focuses) {
        AlchemicalCauldronRecipe recipe = holder.value();
        builder.addSlot(RecipeIngredientRole.INPUT, 6, 7).addIngredients(recipe.getFluid().map(in -> in, fl -> Ingredient.of(fl.getFluid().getBucket())));
        builder.addSlot(RecipeIngredientRole.INPUT, 30, 7).addIngredients(recipe.getIngredient());
        builder.addSlot(RecipeIngredientRole.OUTPUT, 78, 25).addItemStack(RecipeUtil.getResultItem(recipe));
    }
}

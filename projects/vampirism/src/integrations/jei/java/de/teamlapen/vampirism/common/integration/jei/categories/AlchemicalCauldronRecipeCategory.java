package de.teamlapen.vampirism.common.integration.jei.categories;

import de.teamlapen.faction.api.factions.skills.ISkill;
import de.teamlapen.faction.common.util.Color;
import de.teamlapen.vampirism.client.gui.screens.AlchemicalCauldronScreen;
import de.teamlapen.vampirism.common.core.ModBlocks;
import de.teamlapen.vampirism.common.integration.jei.VampirismJEIPlugin;
import de.teamlapen.vampirism.common.world.items.recipes.AlchemicalCauldronRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.display.SlotDisplay;


public class AlchemicalCauldronRecipeCategory implements IRecipeCategory<RecipeHolder<AlchemicalCauldronRecipe>> {
    private final Component localizedName;
    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawableAnimated flame;
    private final IDrawableAnimated arrow;
    private final IDrawableAnimated bubbles;


    public AlchemicalCauldronRecipeCategory(IGuiHelper guiHelper) {
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

    public static Identifier fixSprite(Identifier spriteLoc) {
        return spriteLoc.withPrefix("textures/gui/sprites/").withSuffix(".png");
    }

    @Override
    public void draw(RecipeHolder<AlchemicalCauldronRecipe> holder, IRecipeSlotsView recipeSlotsView, GuiGraphicsExtractor graphics, double mouseX, double mouseY) {
        this.background.draw(graphics);
        graphics.pose().pushMatrix();
        AlchemicalCauldronRecipe recipe = holder.value();
        this.flame.draw(graphics, 19, 27);
        this.arrow.draw(graphics, 41, 25);
        this.bubbles.draw(graphics, 104, 19);
        Minecraft minecraft = Minecraft.getInstance();
        int x = 0;
        int y = 65;
        if (recipe.getRequiredLevel() > 1) {
            Component level = Component.translatable("gui.vampirism.alchemical_cauldron.level", recipe.getRequiredLevel());
            graphics.text(minecraft.font, level, x, y, Color.GRAY.getRGB(), false);
            y += minecraft.font.lineHeight + 2;
        }
        if (!recipe.getRequiredSkills().isEmpty()) {
            MutableComponent skillText = Component.translatable("gui.vampirism.alchemical_cauldron.skill", " ");

            for (Holder<ISkill<?>> s : recipe.getRequiredSkills()) {
                skillText.append(s.value().getName()).append(" ");
            }
            graphics.textWithWordWrap(minecraft.font, skillText, x, y, 132, Color.GRAY.getRGB(), false);
        }
        graphics.pose().popMatrix();
    }

    @Override
    public int getHeight() {
        return 103;
    }

    @Override
    public int getWidth() {
        return 120;
    }

    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public IRecipeType<RecipeHolder<AlchemicalCauldronRecipe>> getRecipeType() {
        return VampirismJEIPlugin.ALCHEMICAL_CAULDRON;
    }

    @Override
    public Component getTitle() {
        return this.localizedName;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<AlchemicalCauldronRecipe> holder, IFocusGroup focuses) {
        AlchemicalCauldronRecipe recipe = holder.value();
        builder.addSlot(RecipeIngredientRole.INPUT, 6, 7).add(recipe.getFluid().<Ingredient>map(x -> x, x -> Ingredient.of(x.fluid().value().getBucket())));
        builder.addSlot(RecipeIngredientRole.INPUT, 30, 7).add(recipe.getIngredient());
        builder.addSlot(RecipeIngredientRole.OUTPUT, 78, 25).add(recipe.result());
        builder.addSlot(RecipeIngredientRole.RENDER_ONLY, 18, 43).add(SlotDisplay.AnyFuel.INSTANCE);
    }
}

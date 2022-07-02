package de.teamlapen.vampirism.modcompat.jei;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.inventory.recipes.AlchemyTableRecipe;
import de.teamlapen.vampirism.util.OilUtils;
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
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.awt.*;

public class AlchemyTableRecipeCategory implements IRecipeCategory<AlchemyTableRecipe> {

    private static final ResourceLocation location = new ResourceLocation(REFERENCE.MODID, "textures/gui/container/alchemy_table.png");

    private final Component localizedName;
    private final IDrawable icon;
    private final IDrawable background;
    private final IDrawableStatic blazeHeat;
    private final IDrawableAnimated arrow;
    private final IDrawableAnimated pool;

    public AlchemyTableRecipeCategory(IGuiHelper helper) {
        this.localizedName = ModBlocks.ALCHEMY_TABLE.get().getName();
        this.background = helper.drawableBuilder(location, 11, 12, 149, 80).addPadding(0,30,0,0).build();
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.ALCHEMY_TABLE.get()));
        this.blazeHeat = helper.createDrawable(location, 176, 9, 18, 4);
        this.arrow = helper.drawableBuilder(location, 176,1, 28,8).buildAnimated(600, IDrawableAnimated.StartDirection.LEFT, false);
        this.pool = helper.drawableBuilder(location, 176,13, 32,32).buildAnimated(600, IDrawableAnimated.StartDirection.LEFT, false);
    }

    @Override
    public RecipeType<AlchemyTableRecipe> getRecipeType() {
        return VampirismJEIPlugin.ALCHEMY_TABLE;
    }

    @Nonnull
    @Override
    public Component getTitle() {
        return this.localizedName;
    }

    @Nonnull
    @Override
    public IDrawable getBackground() {
        return this.background;
    }

    @Nonnull
    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, AlchemyTableRecipe recipe, @NotNull IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 4, 13).addIngredients(recipe.getInput());
        builder.addSlot(RecipeIngredientRole.INPUT, 44,4).addIngredients(recipe.getIngredient());
        builder.addSlot(RecipeIngredientRole.INPUT, 68,4).addIngredients(recipe.getIngredient());
        builder.addSlot(RecipeIngredientRole.OUTPUT, 101,60).addItemStack(recipe.getResultItem());
        builder.addSlot(RecipeIngredientRole.OUTPUT, 129,32).addItemStack(recipe.getResultItem());
        builder.addSlot(RecipeIngredientRole.INPUT, 23,57).addItemStack(new ItemStack(Items.BLAZE_POWDER));
    }

    @Override
    public void draw(@Nonnull AlchemyTableRecipe recipe, IRecipeSlotsView recipeSlotsView, @Nonnull PoseStack matrixStack, double mouseX, double mouseY) {
        this.blazeHeat.draw(matrixStack,33-9-2,60-10-2);
        this.arrow.draw(matrixStack,73-9-2,57-10-2);

        int color = OilUtils.getOil(recipe.getResultItem()).getColor();
        RenderSystem.setShaderColor(((color>> 16) & 0xFF) / 255f,((color >> 8) & 0xFF) / 255f,((color) & 0xFF)/255f,1F);
        this.pool.draw(matrixStack,104-9-2,36-10-2);
        RenderSystem.setShaderColor(1,1,1,1);

        int x = 2;
        int y = 80;
        Minecraft minecraft = Minecraft.getInstance();

        ISkill[] requiredSkills = recipe.getRequiredSkills();
        if (requiredSkills.length > 0) {
            MutableComponent skillText = Component.translatable("gui.vampirism.hunter_weapon_table.skill", " ");

            for (ISkill skill : recipe.getRequiredSkills()) {
                skillText.append(skill.getName()).append(" ");

            }
            y += UtilLib.renderMultiLine(minecraft.font, matrixStack, skillText, 132, x, y, Color.gray.getRGB());

        }
    }
}

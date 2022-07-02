package de.teamlapen.vampirism.modcompat.jei;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.inventory.recipes.AlchemyTableRecipe;
import de.teamlapen.vampirism.util.OilUtils;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class AlchemyTableRecipeCategory implements IRecipeCategory<AlchemyTableRecipe> {

    private static final ResourceLocation location = new ResourceLocation(REFERENCE.MODID, "textures/gui/container/alchemy_table.png");

    private final String localizedName;
    private final IDrawable icon;
    private final IDrawable background;
    private final IDrawableStatic blazeHeat;
    private final IDrawableAnimated arrow;
    private final IDrawableAnimated pool;

    public AlchemyTableRecipeCategory(IGuiHelper helper) {
        this.localizedName = UtilLib.translate(ModBlocks.ALCHEMY_TABLE.get().getDescriptionId());
        this.background = helper.drawableBuilder(location, 11, 12, 149, 80).addPadding(0,30,0,0).build();
        this.icon = helper.createDrawableIngredient(new ItemStack(ModBlocks.ALCHEMY_TABLE.get()));
        this.blazeHeat = helper.createDrawable(location, 176, 9, 18, 4);
        this.arrow = helper.drawableBuilder(location, 176,1, 28,8).buildAnimated(600, IDrawableAnimated.StartDirection.LEFT, false);
        this.pool = helper.drawableBuilder(location, 176,13, 32,32).buildAnimated(600, IDrawableAnimated.StartDirection.LEFT, false);
    }

    @Nonnull
    @Override
    public ResourceLocation getUid() {
        return VampirismJEIPlugin.ALCHEMY_TABLE_UID;
    }

    @Nonnull
    @Override
    public Class<? extends AlchemyTableRecipe> getRecipeClass() {
        return AlchemyTableRecipe.class;
    }

    @Nonnull
    @Override
    public String getTitle() {
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
    public void setIngredients(AlchemyTableRecipe recipe, IIngredients ingredients) {
        List<Ingredient> ingredientList = new ArrayList<>();

        ingredientList.add(recipe.getInput());
        ingredientList.addAll(recipe.getIngredients());

        ingredients.setInputIngredients(ingredientList);

        ingredients.setOutput(VanillaTypes.ITEM, recipe.getResultItem());
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, @Nonnull AlchemyTableRecipe recipe, IIngredients ingredients) {
        IGuiItemStackGroup group = recipeLayout.getItemStacks();
        group.init(0, true, 3,12);
        group.init(1, true, 43,3);
        group.init(2, true, 67,3);
        group.init(3, false, 100,59);
        group.init(4, false, 128,31);
        group.init(5, true, 22,56);

        List<List<ItemStack>> inputs = ingredients.getInputs(VanillaTypes.ITEM);
        List<List<ItemStack>> outputs = ingredients.getOutputs(VanillaTypes.ITEM);

        for (int i = 0; i < inputs.size(); i++) {
            if (i == 0) {
                group.set(i, inputs.get(i));
            } else if (i == 1) {
                group.set(1, inputs.get(i));
                group.set(2, inputs.get(i));

            }
        }

        for (List<ItemStack> output : outputs) {
            for (ItemStack stack : output) {
                group.set(3, stack);
                group.set(4, stack);
            }
        }

        group.set(5, new ItemStack(Items.BLAZE_POWDER));
    }

    @Override
    public void draw(@Nonnull AlchemyTableRecipe recipe, @Nonnull MatrixStack matrixStack, double mouseX, double mouseY) {
        this.blazeHeat.draw(matrixStack,33-9-2,60-10-2);
        this.arrow.draw(matrixStack,73-9-2,57-10-2);

        int color = OilUtils.getOil(recipe.getResultItem()).getColor();
        RenderSystem.color4f(((color>> 16) & 0xFF) / 255f,((color >> 8) & 0xFF) / 255f,((color) & 0xFF)/255f,1F);
        this.pool.draw(matrixStack,104-9-2,36-10-2);
        RenderSystem.color4f(1,1,1,1);

        int x = 2;
        int y = 80;
        Minecraft minecraft = Minecraft.getInstance();

        ISkill[] requiredSkills = recipe.getRequiredSkills();
        if (requiredSkills.length > 0) {
            IFormattableTextComponent skillText = new TranslationTextComponent("gui.vampirism.hunter_weapon_table.skill", " ");

            for (ISkill skill : recipe.getRequiredSkills()) {
                skillText.append(skill.getName()).append(" ");

            }
            y += UtilLib.renderMultiLine(minecraft.font, matrixStack, skillText, 132, x, y, Color.gray.getRGB());

        }
    }
}

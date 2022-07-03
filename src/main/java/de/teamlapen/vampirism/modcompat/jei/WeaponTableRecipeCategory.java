package de.teamlapen.vampirism.modcompat.jei;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.lib.util.Color;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.items.IWeaponTableRecipe;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.inventory.recipes.ShapedWeaponTableRecipe;
import de.teamlapen.vampirism.inventory.recipes.ShapelessWeaponTableRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Recipe category for {@link IWeaponTableRecipe}
 */
public class WeaponTableRecipeCategory implements IRecipeCategory<IWeaponTableRecipe> {

    private final static ResourceLocation location = new ResourceLocation(REFERENCE.MODID, "textures/gui/weapon_table_clean.png");
    private static final ItemStack lavaStack = new ItemStack(Items.LAVA_BUCKET);
    private final Component localizedName;
    private final IDrawable background;
    private final IDrawable icon;


    WeaponTableRecipeCategory(IGuiHelper guiHelper) {
        localizedName = Component.translatable(ModBlocks.WEAPON_TABLE.get().getDescriptionId());
        background = guiHelper.drawableBuilder(location, 32, 14, 134, 77).addPadding(0, 33, 0, 0).build();
        icon = guiHelper.createDrawableIngredient(new ItemStack(ModBlocks.WEAPON_TABLE.get()));
    }

    @Override
    public void draw(IWeaponTableRecipe recipe, @Nonnull PoseStack stack, double mouseX, double mouseY) {

        int x = 2;
        int y = 80;
        Minecraft minecraft = Minecraft.getInstance();
        if (recipe.getRequiredLavaUnits() > 0) {
            stack.pushPose();
            RenderSystem.backupProjectionMatrix();
            RenderSystem.setProjectionMatrix(stack.last().pose());
            minecraft.getItemRenderer().renderGuiItem(lavaStack, 83, 13);
            RenderSystem.restoreProjectionMatrix();
            stack.popPose();
        }
        if (recipe.getRequiredLevel() > 1) {
            Component level = Component.translatable("gui.vampirism.hunter_weapon_table.level", recipe.getRequiredLevel());

            minecraft.font.draw(stack, level, x, y, Color.GRAY.getRGB());
            y += minecraft.font.lineHeight + 2;
        }
        ISkill<?>[] requiredSkills = recipe.getRequiredSkills();
        if (requiredSkills.length > 0) {
            MutableComponent skillText = Component.translatable("gui.vampirism.hunter_weapon_table.skill", " ");

            for (ISkill<?> skill : recipe.getRequiredSkills()) {
                skillText.append(skill.getName()).append(" ");

            }
            y += UtilLib.renderMultiLine(minecraft.font, stack, skillText, 132, x, y, Color.GRAY.getRGB());

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
    public Class<? extends IWeaponTableRecipe> getRecipeClass() {
        return IWeaponTableRecipe.class;
    }

    @Nonnull
    @Override
    public Component getTitle() {
        return localizedName;
    }

    @Nonnull
    @Override
    public ResourceLocation getUid() {
        return VampirismJEIPlugin.WEAPON_TABLE;
    }

    @Override
    public void setIngredients(IWeaponTableRecipe recipe, IIngredients ingredients) {
        ingredients.setInputIngredients(recipe.getIngredients());
        ingredients.setOutput(VanillaTypes.ITEM, recipe.getResultItem());
    }

    @Override
    public void setRecipe(IRecipeLayout iRecipeLayout, @Nonnull IWeaponTableRecipe recipe, @Nonnull IIngredients ingredients) {
        int craftOutputSlot = 0;
        IGuiItemStackGroup guiItemStackGroup = iRecipeLayout.getItemStacks();
        guiItemStackGroup.init(craftOutputSlot, false, 111, 31);
        for (int y = 0; y < 4; ++y) {
            for (int x = 0; x < 4; ++x) {
                guiItemStackGroup.init(1 + x + y * 4, true, 1 + x * 19, 1 + y * 19);
            }
        }

        List<List<ItemStack>> inputs = ingredients.getInputs(VanillaTypes.ITEM);
        List<List<ItemStack>> outputs = ingredients.getOutputs(VanillaTypes.ITEM);

        if (recipe instanceof ShapedWeaponTableRecipe wrapper) {
            setInputs(guiItemStackGroup, inputs, wrapper.getWidth(), wrapper.getHeight());

        } else if (recipe instanceof ShapelessWeaponTableRecipe) {
            setInputs(guiItemStackGroup, inputs);
            iRecipeLayout.setShapeless();
        }


        guiItemStackGroup.set(craftOutputSlot, outputs.get(0));
    }

    private int getCraftingIndex(int i, int width, int height) {
        int index;
        if (width == 1) {
            if (height == 4) {
                index = i * 4 + 1;
            } else if (height == 3) {
                index = i * 4 + 1;
            } else if (height == 2) {
                index = i * 4 + 1;
            } else {
                index = 0;
            }
        } else if (height == 1) {
            index = i + 4;
        } else if (width == 2) {
            index = i;
            if (i > 1) {
                index += 2;
                if (i > 3) {
                    index += 2;
                }
            }
        } else if (width == 3) {
            index = i;
            if (i > 2) {
                index++;
                if (i > 5) {
                    index++;
                    if (i > 8) {
                        index++;
                    }
                }
            }
        } else if (height == 2) {
            index = i + 4;
        } else {
            index = i;
        }

        return index;
    }

    private void setInputs(IGuiItemStackGroup ingredientGroup, List<List<ItemStack>> inputs) {
        byte width;
        byte height;
        if (inputs.size() > 4) {
            height = 4;
            width = 4;
        } else if (inputs.size() > 1) {
            height = 2;
            width = 2;
        } else {
            height = 1;
            width = 1;
        }
        this.setInputs(ingredientGroup, inputs, width, height);
    }

    private void setInputs(IGuiItemStackGroup ingredientGroup, List<List<ItemStack>> inputs, int width, int height) {
        for (int i = 0; i < inputs.size(); i++) {
            List<ItemStack> recipeItem = inputs.get(i);
            int index = this.getCraftingIndex(i, width, height);
            ingredientGroup.set(1 + index, recipeItem);
        }
    }


}

package de.teamlapen.vampirism.modcompat.jei;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexSorting;
import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.lib.util.Color;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.items.IWeaponTableRecipe;
import de.teamlapen.vampirism.client.gui.screens.WeaponTableScreen;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.recipes.ShapelessWeaponTableRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.common.crafting.IShapedRecipe;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Recipe category for {@link IWeaponTableRecipe}
 */
public class WeaponTableRecipeCategory implements IRecipeCategory<RecipeHolder<IWeaponTableRecipe>> {

    private static final ItemStack lavaStack = new ItemStack(Items.LAVA_BUCKET);
    private final @NotNull Component localizedName;
    private final @NotNull IDrawable background;
    private final @NotNull IDrawable icon;
    private final @NotNull IDrawable bucket;

    WeaponTableRecipeCategory(@NotNull IGuiHelper guiHelper) {
        this.localizedName = Component.translatable(ModBlocks.WEAPON_TABLE.get().getDescriptionId());
        this.background = guiHelper.drawableBuilder(WeaponTableScreen.BACKGROUND, 32, 14, 134, 77).addPadding(0, 33, 0, 0).build();
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.WEAPON_TABLE.get()));
        this.bucket = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(Items.LAVA_BUCKET));
    }

    @Override
    public void draw(@NotNull RecipeHolder<IWeaponTableRecipe> holder, @NotNull IRecipeSlotsView recipeSlotsView, @NotNull GuiGraphics graphics, double mouseX, double mouseY) {
        IWeaponTableRecipe recipe = holder.value();

        int x = 2;
        int y = 80;
        Minecraft minecraft = Minecraft.getInstance();
        if (recipe.getRequiredLavaUnits() > 0) {
            this.bucket.draw(graphics, 83, 13);
            PoseStack pose = graphics.pose();
            pose.pushPose();
            RenderSystem.backupProjectionMatrix();
            RenderSystem.setProjectionMatrix(pose.last().pose(), VertexSorting.ORTHOGRAPHIC_Z);
            graphics.renderItem(lavaStack, 83, 13);
            RenderSystem.restoreProjectionMatrix();
            pose.popPose();
        }
        if (recipe.getRequiredLevel() > 1) {
            Component level = Component.translatable("gui.vampirism.hunter_weapon_table.level", recipe.getRequiredLevel());

            graphics.drawString(minecraft.font, level, x, y, Color.GRAY.getRGB(), false);
            y += minecraft.font.lineHeight + 2;
        }
        List<Holder<ISkill<?>>> requiredSkills = recipe.getRequiredSkills();
        if (!requiredSkills.isEmpty()) {
            MutableComponent skillText = Component.translatable("gui.vampirism.skill_required", " ");

            for (Holder<ISkill<?>> skill : recipe.getRequiredSkills()) {
                skillText.append(skill.value().getName()).append(" ");

            }
            y += UtilLib.renderMultiLine(minecraft.font, graphics, skillText, 132, x, y, Color.GRAY.getRGB());

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

    @Override
    public @NotNull RecipeType<RecipeHolder<IWeaponTableRecipe>> getRecipeType() {
        return VampirismJEIPlugin.WEAPON_TABLE;
    }

    @NotNull
    @Override
    public Component getTitle() {
        return localizedName;
    }


    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<IWeaponTableRecipe> holder, IFocusGroup focuses) {
        IWeaponTableRecipe recipe = holder.value();
        if (recipe instanceof ShapelessWeaponTableRecipe) {
            builder.setShapeless();
        }
        List<List<ItemStack>> inputs = recipe.getIngredients().stream()
                .map(ingredient -> List.of(ingredient.getItems()))
                .toList();
        List<List<ItemStack>> list = recipe.getIngredients().stream().map(s -> List.of(s.getItems())).toList();
        IRecipeSlotBuilder output = builder.addSlot(RecipeIngredientRole.OUTPUT, 112, 32);
        output.addItemStack(RecipeUtil.getResultItem(recipe));

        List<IRecipeSlotBuilder> inputSlots = new ArrayList<>();
        for (int y = 0; y < 4; ++y) {
            for (int x = 0; x < 4; ++x) {
                IRecipeSlotBuilder slot = builder.addSlot(RecipeIngredientRole.INPUT, 2 + x * 19, 2 + y * 19);
                inputSlots.add(slot);
            }
        }

        int height = recipe instanceof IShapedRecipe<?> shaped ? shaped.getHeight() : 4;
        int width = recipe instanceof IShapedRecipe<?> shaped ? shaped.getWidth() : 4;

        for (int i = 0; i < inputs.size(); i++) {
            int index = getCraftingIndex(i, width, height);
            IRecipeSlotBuilder slot = inputSlots.get(index);
            List<ItemStack> itemStacks = inputs.get(i);
            if (itemStacks != null) {
                slot.addIngredients(VanillaTypes.ITEM_STACK, itemStacks);
            }
        }
    }

    private static int getCraftingIndex(int i, int width, int height) {
        int index;
        if (width == 1) {
            if (height == 4) {
                index = (i * 4) + 1;
            } else if (height == 3) {
                index = (i * 4) + 1;
            } else if (height == 2) {
                index = (i * 4) + 1;
            } else {
                index = 5;
            }
        } else if (height == 1) {
            index = i + 4;
        } else if (width == 2) {
            index = i;
            if (i > 1) {
                index += 2;
                if (i > 3) {
                    index += 2;
                    if (i > 5) {
                        index += 2;
                    }
                }
            }
        } else if (height == 2) {
            index = i;
            if (width == 3 && i > 2) {
                index++;
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
        } else {
            index = i;
        }
        return index;
    }
}

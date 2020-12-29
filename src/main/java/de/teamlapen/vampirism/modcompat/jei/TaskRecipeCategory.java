package de.teamlapen.vampirism.modcompat.jei;


import com.mojang.blaze3d.matrix.MatrixStack;
import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.task.Task;
import de.teamlapen.vampirism.api.entity.player.task.TaskReward;
import de.teamlapen.vampirism.api.entity.player.task.TaskUnlocker;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.player.tasks.reward.ItemReward;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.awt.*;
import java.util.List;

public class TaskRecipeCategory implements IRecipeCategory<Task> {
    private final IDrawable background;
    private final IDrawable icon;

    public TaskRecipeCategory(IGuiHelper guiHelper) {
        background = guiHelper.drawableBuilder(new ResourceLocation("jei", "textures/gui/slot.png"), 0, 0, 18, 18).setTextureSize(18, 18).addPadding(14, 90, 75, 75).build();
        icon = guiHelper.createDrawableIngredient(new ItemStack(ModItems.vampire_fang));
    }

    @Override
    public void draw(Task task, MatrixStack stack, double mouseX, double mouseY) {
        Minecraft minecraft = Minecraft.getInstance();
        int x = 4;
        int y = 40;
        minecraft.fontRenderer.func_243248_b(stack, task.getTranslation(), 1, 1, Color.gray.getRGB());
        IPlayableFaction<?> f = task.getFaction();
        ITextComponent taskmasterComponent = f == null || f.getVillageData().getTaskMasterEntity() == null ? new TranslationTextComponent("text.vampirism.faction_representative") : new TranslationTextComponent(f.getVillageData().getTaskMasterEntity().getTranslationKey());
        ITextComponent text = new TranslationTextComponent("text.vampirism.task.reward_obtain", taskmasterComponent);
        y += UtilLib.renderMultiLine(minecraft.fontRenderer, stack, text, 160, x, y, Color.gray.getRGB());

        IFormattableTextComponent prerequisites = new TranslationTextComponent("text.vampirism.task.prerequisites").appendString(":\n");
        TaskUnlocker[] unlockers = task.getUnlocker();
        if (unlockers.length > 0) {
            StringTextComponent newLine = new StringTextComponent("\n");
            for (TaskUnlocker u : unlockers) {
                prerequisites.append(new StringTextComponent("- ")).append(u.getDescription()).append(newLine);
            }
        } else {
            prerequisites.append(new TranslationTextComponent("text.vampirism.task.prerequisites.none"));
        }
        y += UtilLib.renderMultiLine(minecraft.fontRenderer, stack, prerequisites, 160, x, y, Color.gray.getRGB());

    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public Class<? extends Task> getRecipeClass() {
        return Task.class;
    }

    @Override
    public String getTitle() {
        return UtilLib.translate("text.vampirism.task.reward");
    }

    @Override
    public ResourceLocation getUid() {
        return VampirismJEIPlugin.TASK_RECIPE_UID;
    }

    @Override
    public void setIngredients(Task recipe, IIngredients ingredients) {
        TaskReward reward = recipe.getReward();
        if (reward instanceof ItemReward) {
            ingredients.setOutput(VanillaTypes.ITEM, ((ItemReward) reward).getReward());
        }
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, Task recipe, IIngredients ingredients) {
        int craftOutputSlot = 0;
        IGuiItemStackGroup guiItemStackGroup = recipeLayout.getItemStacks();
        guiItemStackGroup.init(craftOutputSlot, false, 75, 14);
        List<List<ItemStack>> outputs = ingredients.getOutputs(VanillaTypes.ITEM);
        guiItemStackGroup.set(craftOutputSlot, outputs.get(0));
    }
}

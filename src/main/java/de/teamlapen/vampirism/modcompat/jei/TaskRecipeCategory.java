package de.teamlapen.vampirism.modcompat.jei;


import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.lib.util.Color;
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
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class TaskRecipeCategory implements IRecipeCategory<Task> {
    private final IDrawable background;
    private final IDrawable icon;

    public TaskRecipeCategory(IGuiHelper guiHelper) {
        background = guiHelper.drawableBuilder(new ResourceLocation("jei", "textures/gui/slot.png"), 0, 0, 18, 18).setTextureSize(18, 18).addPadding(14, 90, 75, 75).build();
        icon = guiHelper.createDrawableIngredient(new ItemStack(ModItems.VAMPIRE_FANG.get()));
    }

    @Override
    public void draw(Task task, @Nonnull PoseStack stack, double mouseX, double mouseY) {
        Minecraft minecraft = Minecraft.getInstance();
        int x = 4;
        int y = 40;
        minecraft.font.draw(stack, task.getTranslation(), 1, 1, Color.GRAY.getRGB());
        IPlayableFaction<?> f = task.getFaction();
        Component taskmasterComponent = f == null || f.getVillageData().getTaskMasterEntity() == null ? Component.translatable("text.vampirism.faction_representative") : Component.translatable(f.getVillageData().getTaskMasterEntity().getDescriptionId());
        Component text = Component.translatable("text.vampirism.task.reward_obtain", taskmasterComponent);
        y += UtilLib.renderMultiLine(minecraft.font, stack, text, 160, x, y, Color.GRAY.getRGB());

        MutableComponent prerequisites = Component.translatable("text.vampirism.task.prerequisites").append(":\n");
        TaskUnlocker[] unlockers = task.getUnlocker();
        if (unlockers.length > 0) {
            Component newLine = Component.literal("\n");
            for (TaskUnlocker u : unlockers) {
                prerequisites.append(Component.literal("- ")).append(u.getDescription()).append(newLine);
            }
        } else {
            prerequisites.append(Component.translatable("text.vampirism.task.prerequisites.none"));
        }
        y += UtilLib.renderMultiLine(minecraft.font, stack, prerequisites, 160, x, y, Color.GRAY.getRGB());

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
    public Class<? extends Task> getRecipeClass() {
        return Task.class;
    }

    @Nonnull
    @Override
    public Component getTitle() {
        return Component.translatable("text.vampirism.task.reward");
    }

    @Nonnull
    @Override
    public ResourceLocation getUid() {
        return VampirismJEIPlugin.TASK;
    }

    @Override
    public void setIngredients(Task recipe, @Nonnull IIngredients ingredients) {
        TaskReward reward = recipe.getReward();
        if (reward instanceof ItemReward) {
            ingredients.setOutputs(VanillaTypes.ITEM, ((ItemReward) reward).getAllPossibleRewards());
        }
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, @Nonnull Task recipe, IIngredients ingredients) {
        int craftOutputSlot = 0;
        IGuiItemStackGroup guiItemStackGroup = recipeLayout.getItemStacks();
        guiItemStackGroup.init(craftOutputSlot, false, 75, 14);
        List<List<ItemStack>> outputs = ingredients.getOutputs(VanillaTypes.ITEM);
        guiItemStackGroup.set(craftOutputSlot, outputs.stream().flatMap(Collection::stream).collect(Collectors.toList()));
    }
}

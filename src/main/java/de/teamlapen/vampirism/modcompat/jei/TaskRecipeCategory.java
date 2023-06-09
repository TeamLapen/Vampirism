package de.teamlapen.vampirism.modcompat.jei;


import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.lib.util.Color;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.VampirismRegistries;
import de.teamlapen.vampirism.api.entity.player.task.Task;
import de.teamlapen.vampirism.api.entity.player.task.TaskReward;
import de.teamlapen.vampirism.api.entity.player.task.TaskUnlocker;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.entity.player.tasks.reward.ItemReward;
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
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Objects;

public class TaskRecipeCategory implements IRecipeCategory<Task> {
    private final @NotNull IDrawable background;
    private final @NotNull IDrawable icon;
    private final IGuiHelper guiHelper;

    public TaskRecipeCategory(@NotNull IGuiHelper guiHelper) {
        this.background = guiHelper.drawableBuilder(new ResourceLocation("jei", "textures/jei/gui/gui_vanilla.png"), 90, 168, 18, 18).addPadding(14, 90, 75, 75).build();
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModItems.VAMPIRE_FANG.get()));
        this.guiHelper = guiHelper;
    }

    @Override
    public void draw(@NotNull Task task, @NotNull IRecipeSlotsView recipeSlotsView, @NotNull GuiGraphics graphics, double mouseX, double mouseY) {
        Minecraft minecraft = Minecraft.getInstance();
        int x = 4;
        int y = 40;
        graphics.drawString(minecraft.font, task.getTitle(), 1, 1, Color.GRAY.getRGB(), false);
        Registry<Task> tasks = minecraft.level.registryAccess().registryOrThrow(VampirismRegistries.TASK_ID);
        Component taskmasterComponent = Arrays.stream(VampirismAPI.factionRegistry().getFactions()).filter(s -> s.getTag(VampirismRegistries.TASK_ID).filter(t -> tasks.wrapAsHolder(task).is(t)).isPresent()).map(a -> a.getVillageData().getTaskMasterEntity()).filter(Objects::nonNull).map(EntityType::getDescriptionId).map(Component::translatable).reduce((comp1, comp2) -> comp1.append(", ").append(comp2)).orElse(Component.translatable("text.vampirism.faction_representative"));
        Component text = Component.translatable("text.vampirism.task.reward_obtain", taskmasterComponent);
        y += UtilLib.renderMultiLine(minecraft.font, graphics, text, 160, x, y, Color.GRAY.getRGB());

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
        y += UtilLib.renderMultiLine(minecraft.font, graphics, prerequisites, 160, x, y, Color.GRAY.getRGB());

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
        return Component.translatable("text.vampirism.task.reward");
    }

    @Override
    public @NotNull RecipeType<Task> getRecipeType() {
        return VampirismJEIPlugin.TASK;
    }

    @Override
    public void setRecipe(@NotNull IRecipeLayoutBuilder builder, @NotNull Task recipe, @NotNull IFocusGroup focuses) {
        TaskReward reward = recipe.getReward();
        if (reward instanceof ItemReward itemReward) {
            IRecipeSlotBuilder output = builder.addSlot(RecipeIngredientRole.OUTPUT, 76, 15);
            output.addItemStacks(itemReward.getAllPossibleRewards());
        }
    }
}

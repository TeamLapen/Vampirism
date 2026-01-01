package de.teamlapen.vampirism.common.integration.jei.categories;


import de.teamlapen.faction.api.FactionRegistries;
import de.teamlapen.faction.api.factions.IFactionTags;
import de.teamlapen.faction.api.factions.tasks.Task;
import de.teamlapen.faction.api.factions.tasks.TaskReward;
import de.teamlapen.faction.api.factions.tasks.TaskUnlocker;
import de.teamlapen.faction.common.core.ModRegistries;
import de.teamlapen.faction.common.factions.tasks.reward.ItemReward;
import de.teamlapen.faction.common.util.Color;
import de.teamlapen.vampirism.common.core.ModItems;
import de.teamlapen.vampirism.common.integration.jei.VampirismJEIPlugin;
import de.teamlapen.vampirism.common.util.UtilLib;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class TaskRecipeCategory implements IRecipeCategory<Task> {

    private final @NotNull IDrawable icon;

    public TaskRecipeCategory(@NotNull IGuiHelper guiHelper) {
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModItems.VAMPIRE_FANG.get()));
    }

    @Override
    public void draw(@NotNull Task task, @NotNull IRecipeSlotsView recipeSlotsView, @NotNull GuiGraphics graphics, double mouseX, double mouseY) {
        Minecraft minecraft = Minecraft.getInstance();
        int x = 4;
        int y = 40;
        graphics.vampirism$drawCenteredString(minecraft.font, task.title(), getWidth()/2, 2, Color.GRAY.getRGB(), false);
        Registry<Task> tasks = minecraft.level.registryAccess().lookupOrThrow(FactionRegistries.Keys.TASK);
        Component taskmasterComponent = ModRegistries.FACTIONS.listElements().filter(s -> IFactionTags.get().get(s, FactionRegistries.Keys.TASK).filter(t -> tasks.wrapAsHolder(task).is(t)).isPresent()).map(a -> a.value().getVillageData().getTaskMasterEntity()).filter(Objects::nonNull).map(EntityType::getDescriptionId).map(Component::translatable).reduce((comp1, comp2) -> comp1.append(", ").append(comp2)).orElse(Component.translatable("text.vampirism.faction_representative"));
        Component text = Component.translatable("text.vampirism.task.reward_obtain", taskmasterComponent);

        y += UtilLib.renderMultiLine(minecraft.font, graphics, text, 160, x, y, Color.GRAY.getRGB());

        MutableComponent prerequisites = Component.translatable("text.vampirism.task.prerequisites").append(":\n");
        List<TaskUnlocker> unlockers = task.unlocker();
        if (!unlockers.isEmpty()) {
            Component newLine = Component.literal("\n");
            for (TaskUnlocker u : unlockers) {
                prerequisites.append(Component.literal("- ")).append(u.getDescription()).append(newLine);
            }
        } else {
            prerequisites.append(Component.translatable("text.vampirism.task.prerequisites.none"));
        }
        UtilLib.renderMultiLine(minecraft.font, graphics, prerequisites, 160, x, y, Color.GRAY.getRGB());
    }

    @Override
    public int getHeight() {
        return 122;
    }

    @Override
    public int getWidth() {
        return 168;
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
    public @NotNull IRecipeType<Task> getRecipeType() {
        return VampirismJEIPlugin.TASK;
    }

    @Override
    public void setRecipe(@NotNull IRecipeLayoutBuilder builder, @NotNull Task recipe, @NotNull IFocusGroup focuses) {
        TaskReward reward = recipe.reward();
        if (reward instanceof ItemReward itemReward) {
            builder.addSlot(RecipeIngredientRole.OUTPUT, 76, 15).setStandardSlotBackground().addItemStacks(itemReward.getAllPossibleRewards());
        }
    }
}

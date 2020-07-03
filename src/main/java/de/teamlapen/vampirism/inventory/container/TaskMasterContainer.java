package de.teamlapen.vampirism.inventory.container;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import de.teamlapen.vampirism.api.entity.player.task.ITaskManager;
import de.teamlapen.vampirism.api.entity.player.task.Task;
import de.teamlapen.vampirism.core.ModContainer;
import de.teamlapen.vampirism.core.ModRegistries;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


public class TaskMasterContainer extends Container {

    private final @Nonnull ITaskManager taskManager;
    private @Nonnull Set<Task> possibleTasks = ImmutableSet.of();
    private @Nonnull Set<Task> completed = Sets.newHashSet();
    private @Nonnull List<Task> availableTasks;

    public TaskMasterContainer(int id, PlayerInventory playerInventory) {
        super(ModContainer.task_master, id);
        this.taskManager = FactionPlayerHandler.get(playerInventory.player).getCurrentFactionPlayer().get().getTaskManager();
        this.availableTasks = Lists.newArrayList(taskManager.getAvailableTasks());
    }

    @Override
    public boolean canInteractWith(@Nonnull PlayerEntity playerIn) {
        return FactionPlayerHandler.getOpt(playerIn).map(player -> player.getCurrentFaction() != null).orElse(false);
    }

    public boolean canCompleteTask(Task task) {
        return possibleTasks.contains(task);
    }

    public void completeTask(Task task) {
        this.taskManager.completeTask(task);
        this.completed.add(task);
        this.possibleTasks.removeIf(task1 -> !taskManager.canCompleteTask(task1));
        this.availableTasks.addAll(ModRegistries.TASKS.getValues().stream().filter(task1 -> task1.requireParent() && task == task1.getParentTask()).collect(Collectors.toList()));
        this.sortTasks();
    }

    public boolean isCompleted(Task task) {
        return this.completed.contains(task);
    }

    public int size() {
        return this.possibleTasks.size();
    }

    @Nonnull
    public List<Task> getAvailableTasks() {
        return this.availableTasks;
    }

    public void setPossibleTasks(@Nonnull Set<Task> possibleTasks) {
        this.possibleTasks = possibleTasks;
        this.sortTasks();
    }

    public TextFormatting getFactionColor() {
        return this.taskManager.getFaction().getChatColor();
    }

    private void sortTasks() {
        this.availableTasks.sort((task1, task2) -> (possibleTasks.contains(task1) && !possibleTasks.contains(task2)) || (!possibleTasks.contains(task1) && !completed.contains(task1) && completed.contains(task2)) ? -1 : 0);
    }

}

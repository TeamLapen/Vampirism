package de.teamlapen.vampirism.inventory.container;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import de.teamlapen.vampirism.api.entity.player.task.ITaskManager;
import de.teamlapen.vampirism.api.entity.player.task.Task;
import de.teamlapen.vampirism.core.ModContainer;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;

import javax.annotation.Nonnull;
import java.util.Set;


public class TaskMasterContainer extends Container {

    private final ITaskManager taskManager;
    private Set<Task> possibleTasks = ImmutableSet.of();
    private Set<Task> completed = Sets.newHashSet();

    public TaskMasterContainer(int id, PlayerInventory playerInventory) {
        super(ModContainer.task_master, id);
        this.taskManager = FactionPlayerHandler.get(playerInventory.player).getCurrentFactionPlayer().get().getTaskManager();
    }

    @Override
    public boolean canInteractWith(@Nonnull PlayerEntity playerIn) {
        return FactionPlayerHandler.getOpt(playerIn).map(player -> player.getCurrentFaction() != null).orElse(false);
    }

    public boolean canCompleteTask(Task task) {
        return possibleTasks != null && possibleTasks.contains(task);
    }

    public void completeTask(Task task) {
        this.completed.add(task);
    }

    public boolean isCompleted(Task task) {
        return this.completed.contains(task);
    }

    public int size() {
        return this.possibleTasks.size();
    }

    public Set<Task> getAvailableTasks() {
        return this.taskManager.getAvailableTasks();
    }

    public void setPossibleTasks(Set<Task> possibleTasks) {
        this.possibleTasks = possibleTasks;
    }
}

package de.teamlapen.vampirism.inventory.container;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import de.teamlapen.vampirism.api.entity.player.task.ITaskManager;
import de.teamlapen.vampirism.api.entity.player.task.Task;
import de.teamlapen.vampirism.core.ModContainer;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nonnull;
import java.util.List;


public class TaskMasterContainer extends Container {

    private final ITaskManager taskManager;
    private List<Task> possibleTasks = ImmutableList.of();
    private List<Task> completed = Lists.newArrayList();

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
        this.taskManager.completeTask(task);
        this.completed.add(task);
    }

    public boolean isCompleted(Task task) {
        return this.completed.contains(task);
    }

    public int size() {
        return this.possibleTasks.size();
    }

    public List<Task> getAvailableTasks() {
        return this.taskManager.getAvailableTasks();
    }

    public void setPossibleTasks(List<Task> possibleTasks) {
        this.possibleTasks = possibleTasks;
    }

    public TextFormatting getFactionColor() {
        return this.taskManager.getFaction().getChatColor();
    }
}

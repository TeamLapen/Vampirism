package de.teamlapen.vampirism.api.entity.player.task;

import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ITaskManager {

    void openTaskMasterScreen(int entityId);

    /**
     * checks if the task can be completed
     * if yes calls {@link #addCompletedTask(Task)} and handles rewards, requirements and sync to client
     *
     * @param task the task to complete
     */
    boolean completeTask(int entityId, @Nonnull Task task);

    void acceptTask(int entityId, @Nonnull Task task);

    void abortTask(int entityId, @Nonnull Task task);

    boolean hasAvailableTasks(int entityId);

    /**
     * syncs completed, available and completable tasks to the client
     */
    void updateClient(int entityId, Map<Task, List<ResourceLocation>> requirements, Set<Task> completable,Set<Task> notAcceptedTasks, Set<Task> available);

    /**
     * @param task the task to check
     * @return whether the task can be completed or not
     */
    boolean canCompleteTask(int entityId, @Nonnull Task task);

    /**
     * resets all completed tasks
     */
    void reset();

    /**
     * @param task the task to check
     * @return whether the task is completed or not
     */
    boolean isUniqueTaskCompleted(@Nonnull Task task);

    void updateTaskLists();

    void resetTaskLists();
}

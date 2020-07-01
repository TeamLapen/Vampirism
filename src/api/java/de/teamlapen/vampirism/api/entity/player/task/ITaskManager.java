package de.teamlapen.vampirism.api.entity.player.task;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

public interface ITaskManager {

    /**
     *
     * checks if the task can be completed
     * if yes calls {@link #addCompletedTask(Task)} and handles rewards, requirements and sync to client
     *
     * @param task the task to complete
     */
    void completeTask(@Nonnull Task task);

    /**
     * if the task is unlocked
     * adds the task as completed and updates related task lists
     *
     * @param task the completed task
     * @return if the task could be added
     */
    boolean addCompletedTask(@Nonnull Task task);

    /**
     *
     * @param variant the variant of the task list
     * @return whether the player has task of the variant available or not
     */
    boolean hasAvailableTasks(@Nullable Task.Variant variant);

    /**
     *
     * @param variant the type of the tasks or {@code null} if all variants should be considered
     * @return all available tasks of type variant
     */
    @Nonnull
    Set<Task> getAvailableTasks(@Nullable Task.Variant variant);

    /**
     * @return all available tasks
     */
    @Nonnull
    Set<Task> getAvailableTasks();

    /**
     *
     * @param variant the type of the tasks or {@code null} if all variants should be considered
     * @return all completed tasks of type variant
     */
    @Nonnull
    Set<Task> getCompletedTasks(@Nullable Task.Variant variant);

    /**
     * @return all completed tasks
     */
    @Nonnull
    Set<Task> getCompletedTasks();

    /**
     *
     * @param variant the type of the tasks or {@code null} if all variants should be considered
     * @return all completable tasks of type variant
     */
    @Nonnull
    Set<Task> getCompletableTasks(@Nullable Task.Variant variant);

    /**
     * @return all completable tasks
     */
    @Nonnull
    Set<Task> getCompletableTasks();

    /**
     * syncs completed, available and completable tasks to the client
     */
    void updateClient();

    /**
     *
     * @param task the task to check
     * @return whether the task can be completed or not
     */
    boolean canCompleteTask(@Nonnull Task task);

    /**
     * resets all completed tasks
     */
    void reset();

    /**
     * initialize usage of the taskmanager
     */
    void init();

    /**
     *
     * @param task the task to check
     * @return whether the task is completed or not
     */
    boolean isTaskCompleted(@Nonnull Task task);
}

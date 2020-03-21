package de.teamlapen.vampirism.api.entity.player.task;

import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Set;

public interface ITaskManager {

    void completeTask(@Nonnull Task task);

    boolean addCompletedTask(@Nonnull Task task);

    boolean hasAvailableTasks(Task.Variant variant);

    @Nonnull
    Set<Task> getAvailableTasks(Task.Variant variant);

    @Nonnull
    Set<Task> getCompletedTasks(Task.Variant variant);

    @Nonnull
    Set<Task> getCompletableTasks(Task.Variant variant);

    boolean canCompleteTask(Task task);

    void removeRequirements(@Nonnull Task task);

    IPlayableFaction<?> getFaction();

    void setCompletedTasks(@Nonnull Collection<Task> tasks);

    void reset();

    void init();

    void applyRewards(Task task);

    boolean isTaskCompleted(Task task);
}

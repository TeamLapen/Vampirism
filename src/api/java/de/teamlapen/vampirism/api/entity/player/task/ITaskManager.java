package de.teamlapen.vampirism.api.entity.player.task;

import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Set;

public interface ITaskManager {

    void completeTask(@Nonnull Task task);

    boolean addCompletedTask(@Nonnull Task task);

    boolean hasAvailableTasks();

    @Nonnull
    Set<Task> getAvailableTasks();

    @Nonnull
    Set<Task> getCompletedTasks();

    @Nonnull
    Set<Task> getCompletableTasks();

    boolean canCompleteTask(Task task);

    void removeRequirements(@Nonnull Task task);

    IPlayableFaction<?> getFaction();

    void setCompletedTasks(@Nonnull Collection<Task> tasks);

    void reset();

    void applyRewards(Task task);
}

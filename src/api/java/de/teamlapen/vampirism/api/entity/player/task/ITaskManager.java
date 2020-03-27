package de.teamlapen.vampirism.api.entity.player.task;

import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;

public interface ITaskManager {

    void completeTask(@Nonnull Task task);

    boolean addCompletedTask(@Nonnull Task task);

    @Nonnull
    List<Task> getAvailableTasks();

    @Nonnull
    List<Task> getCompletedTasks();

    @Nonnull
    List<Task> getCompletableTasks();

    IPlayableFaction<?> getFaction();

    void setCompletedTasks(@Nonnull Collection<Task> tasks);

    void reset();
}

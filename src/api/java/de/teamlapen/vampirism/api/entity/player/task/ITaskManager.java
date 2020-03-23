package de.teamlapen.vampirism.api.entity.player.task;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Set;

public interface ITaskManager {

    void completeTask(@Nonnull Task task);

    boolean addCompletedTask(@Nonnull Task task);

    @Nonnull
    Set<Task> getAvailableTasks();

    @Nonnull
    Set<Task> getCompletedTasks();

    @Nonnull
    Set<Task> getCompletableTasks();

    void setCompletedTasks(@Nonnull Collection<Task> tasks);

    void reset();
}

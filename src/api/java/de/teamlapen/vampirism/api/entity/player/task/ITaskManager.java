package de.teamlapen.vampirism.api.entity.player.task;

import javax.annotation.Nonnull;
import java.util.Set;

public interface ITaskManager {

    void completeTask(@Nonnull Task task);

    @Nonnull
    Set<Task> getAvailableTasks();

    @Nonnull
    Set<Task> getCompletedTasks();

    void reset();
}

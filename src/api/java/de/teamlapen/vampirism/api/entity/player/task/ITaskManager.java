package de.teamlapen.vampirism.api.entity.player.task;

import javax.annotation.Nonnull;

public interface ITaskManager {

    void completeTask(@Nonnull Task task);

    void reset();
}

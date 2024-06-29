package de.teamlapen.vampirism.api.entity.player;

import de.teamlapen.vampirism.api.entity.player.task.ITaskManager;
import org.jetbrains.annotations.NotNull;

public interface ITaskPlayer<T extends IFactionPlayer<T> & ITaskPlayer<T>> extends IFactionPlayer<T> {

    /**
     * null on client & @NotNull on server
     */
    @NotNull
    ITaskManager getTaskManager();
}

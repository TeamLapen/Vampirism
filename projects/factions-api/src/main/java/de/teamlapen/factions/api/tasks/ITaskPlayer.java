package de.teamlapen.factions.api.tasks;

import de.teamlapen.factions.api.entities.player.IFactionPlayer;
import org.jetbrains.annotations.NotNull;

public interface ITaskPlayer<T extends IFactionPlayer<T> & ITaskPlayer<T>> extends IFactionPlayer<T> {

    /**
     * null on client & @NotNull on server
     */
    @NotNull
    ITaskManager getTaskManager();
}

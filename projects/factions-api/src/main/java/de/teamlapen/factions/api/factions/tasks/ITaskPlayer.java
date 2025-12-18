package de.teamlapen.factions.api.factions.tasks;

import de.teamlapen.factions.api.world.entities.player.IFactionPlayer;

public interface ITaskPlayer<T extends IFactionPlayer<T> & ITaskPlayer<T>> extends IFactionPlayer<T> {

    /**
     * null on client and @NotNull on server
     */
    ITaskManager getTaskManager();
}

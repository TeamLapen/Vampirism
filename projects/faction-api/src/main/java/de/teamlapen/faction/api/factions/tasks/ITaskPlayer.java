package de.teamlapen.faction.api.factions.tasks;

import de.teamlapen.faction.api.world.entities.player.IFactionPlayer;

public interface ITaskPlayer<T extends IFactionPlayer<T> & ITaskPlayer<T>> extends IFactionPlayer<T> {

    static <T extends ITaskPlayer<T>> Optional<T> get(Player player) {
        return FactionsApi.factionPlayerHandler(player).getTaskPlayer();
    }
    /**
     * null on client and @NotNull on server
     */
    ITaskManager getTaskManager();
}

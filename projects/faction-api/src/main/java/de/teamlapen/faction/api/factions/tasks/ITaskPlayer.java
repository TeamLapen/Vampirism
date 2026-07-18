package de.teamlapen.faction.api.factions.tasks;

import de.teamlapen.faction.api.FactionsApi;
import de.teamlapen.faction.api.world.entities.player.IFactionPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.Optional;

public interface ITaskPlayer<T extends IFactionPlayer<T> & ITaskPlayer<T>> extends IFactionPlayer<T> {

    static <T extends ITaskPlayer<T>> Optional<T> get(Player player) {
        return FactionsApi.factionPlayerHandler(player).getTaskPlayer();
    }
    /**
     * null on client and @NotNull on server
     */
    ITaskManager getTaskManager();
}

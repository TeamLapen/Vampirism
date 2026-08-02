package de.teamlapen.faction.api.world.entities.minion;

import de.teamlapen.faction.api.factions.IFactionEntity;
import de.teamlapen.faction.api.factions.lord.ILordPlayer;
import org.jetbrains.annotations.ApiStatus;

import java.util.Optional;

/**
 * Entity that is able to follow tasks from a lord player.
 * The minion tries to retrieve its minion data on world join.
 * If the minion data is not available, the Optional methods in this interface will likely be empty
 */
public interface IMinionEntity extends IFactionEntity {

    /**
     * @return The description of the currently executed task. Empty if minion data is not available
     */
    Optional<IMinionTask.IMinionTaskDesc<?>> getCurrentTask();

    /**
     * @return The minion inventory. Empty if minion data is not available
     */
    Optional<IMinionInventory> getInventory();

    /**
     * @return The lord player. Empty if minion data is not available or lord is not loaded
     */
    Optional<ILordPlayer> getLordOpt();

    /**
     * @return The id of this minion. Empty if failed to check out minion data
     */
    Optional<Integer> getMinionId();

    /**
     * DON't call as slot is not freed
     * Called to remove entity from world on call from lord.
     * Does checkin minion
     */
    @ApiStatus.Internal
    void recallMinion();
}

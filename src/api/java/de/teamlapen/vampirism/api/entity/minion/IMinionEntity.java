package de.teamlapen.vampirism.api.entity.minion;

import de.teamlapen.vampirism.api.entity.factions.IFactionEntity;
import de.teamlapen.vampirism.api.entity.player.ILordPlayer;

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
     * TODO clarify deprecation
     */
    @Deprecated
    void recallMinion();
}

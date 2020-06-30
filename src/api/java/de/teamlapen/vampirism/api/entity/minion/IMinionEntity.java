package de.teamlapen.vampirism.api.entity.minion;

import de.teamlapen.vampirism.api.entity.factions.IFactionEntity;
import de.teamlapen.vampirism.api.entity.player.ILordPlayer;

import javax.annotation.Nonnull;
import java.util.Optional;


public interface IMinionEntity extends IFactionEntity {
    Optional<IMinionTask.IMinionTaskDesc> getCurrentTask();

    Optional<IMinionInventory> getInventory();

    @Nonnull
    Optional<ILordPlayer> getLordOpt();

    Optional<Integer> getMinionId();

    /**
     * DON't call as slot is not freed
     * Called to remove entity from world on call from lord.
     * Does checkin minion
     */
    @Deprecated
    void recallMinion();
}

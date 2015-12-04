package de.teamlapen.vampirism.api.entity.player;

import de.teamlapen.vampirism.api.entity.IVampire;
import de.teamlapen.vampirism.api.entity.minions.IMinionLord;

/**
 * Interface for the player vampire data
 */
public interface IVampirePlayer extends IVampire,IFractionPlayer,IMinionLord {
    int getBloodLevel();


    /**
     * @return Whether automatically filling blood into bottles is enabled or not.
     */
    boolean isAutoFillEnabled();
    boolean isVampireLord();


    boolean canTurnOthers();
}

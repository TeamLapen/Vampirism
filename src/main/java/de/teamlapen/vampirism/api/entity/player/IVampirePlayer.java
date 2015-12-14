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

    /**
     * Add an exhaustion modifier (used in blood usage)
     *
     * @param id  ID to remove it later
     * @param mod Exhaustion is multiplied with this
     */
    void addExhaustionModifier(String id, float mod);

    /**
     * Removes a modifier registered with {@link #addExhaustionModifier(String, float)}
     *
     * @param id
     */
    void removeExhaustionModifier(String id);
}

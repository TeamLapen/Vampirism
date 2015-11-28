package de.teamlapen.vampirism.api.entity.player;

import de.teamlapen.vampirism.api.entity.IVampire;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Interface for the player vampire data
 */
public interface IVampirePlayer extends IVampire{
    int getBloodLevel();
    int getLevel();
    EntityPlayer getRepresentingPlayer();

    /**
     * @return Whether automatically filling blood into bottles is enabled or not.
     */
    boolean isAutoFillEnabled();
    boolean isVampireLord();

    /**
     * Sets the vampire level.
     * TODO check that it is a area check exists
     * @param level
     */
    void setLevel(int level);

    /**
     * Sends a sync packet to the client
     * @param all Whether to send it to all players around or only to the corresponding player
     */
    void sync(boolean all);
    boolean canTurnOthers();
}

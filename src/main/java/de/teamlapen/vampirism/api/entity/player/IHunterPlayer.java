package de.teamlapen.vampirism.api.entity.player;

import net.minecraft.entity.player.EntityPlayer;

/**
 * Interface for the hunter player data
 */
public interface IHunterPlayer {

    EntityPlayer getRepresentingPlayer();
    /**
     * Sends a sync packet to the client
     * @param all Whether to send it to all players around or only to the corresponding player
     */
    void sync(boolean all);
}

package de.teamlapen.vampirism.api.entity.player.hunter;

import de.teamlapen.vampirism.api.entity.IHunter;
import de.teamlapen.vampirism.api.entity.minions.IMinionLord;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Interface for the hunter player data
 */
public interface IHunterPlayer extends IFactionPlayer, IHunter, IMinionLord {


    /**
     * Copys all values from another player's IFactionPlayer
     *
     * @param old
     */
    void copyFrom(EntityPlayer old);

}

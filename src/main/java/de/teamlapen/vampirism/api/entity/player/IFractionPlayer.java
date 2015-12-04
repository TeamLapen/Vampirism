package de.teamlapen.vampirism.api.entity.player;

import de.teamlapen.vampirism.api.entity.minions.IMinionLord;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;

/**
 * Basic interface for all of Vampirism's player types (VampirePlayer, HunterPlayer, ...)
 * The player can have levels.
 * A player can only be part of one faction at once, this means only on IFaction ExtendedProperties belonging to one and the same player can have a level above one at once.
 */
public interface IFractionPlayer {
    /**
     * @return 0 if the player is not part of this faction, something > 0 if the player is part of the faction.
     */
    int getLevel();
    EntityPlayer getRepresentingPlayer();
    /**
     * Sets the vampire level.
     * TODO check that it is a area check exists
     * @param level
     */
    void setLevel(int level);

    void levelUp();

    /**
     * Copys all values from another player's IFractionPlayer
     * @param old
     */
    void copyFrom(EntityPlayer old);


}

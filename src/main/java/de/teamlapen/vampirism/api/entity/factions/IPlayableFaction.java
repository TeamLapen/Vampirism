package de.teamlapen.vampirism.api.entity.factions;

import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Represents one playable faction (e.g. Vampire Player)
 * One instance should be used for players and entities at the same time.
 */
public interface IPlayableFaction<T extends IFactionPlayer> extends IFaction<T> {
    /**
     * @return Highest reachable level for players
     */
    int getHighestReachableLevel();

    /**
     * @param player
     * @return The IExtendedEntityProp of this faction for the given player
     */
    T getPlayerProp(EntityPlayer player);

    /**
     * @return The player IExtendedEntityProp  key
     */
    String prop();

    /**
     * @return If the level should be rendered
     */
    boolean renderLevel();

    /**
     * Set if the level should be rendered, default is true
     *
     * @param render
     */
    IPlayableFaction<T> setRenderLevel(boolean render);

}

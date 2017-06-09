package de.teamlapen.vampirism.api.entity.factions;

import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

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
     * @return Unique key of this faction
     */
    ResourceLocation getKey();

    /**
     * @param player
     * @return The "<? extends IFactionPlayer>" of this faction for the given player
     */
    T getPlayerCapability(EntityPlayer player);

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

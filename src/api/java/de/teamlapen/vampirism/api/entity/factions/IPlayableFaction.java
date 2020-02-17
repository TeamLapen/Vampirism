package de.teamlapen.vampirism.api.entity.factions;

import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.common.util.LazyOptional;

/**
 * Represents one playable faction (e.g. Vampire Player)
 * One instance should be used for players and entities at the same time.
 */
public interface IPlayableFaction<T extends IFactionPlayer> extends IFaction<T> {
    Class<T> getFactionPlayerInterface();

    /**
     * @return Highest reachable level for players
     */
    int getHighestReachableLevel();


    /**
     * @param player
     * @return The "<? extends IFactionPlayer>" of this faction for the given player
     */
    LazyOptional<T> getPlayerCapability(PlayerEntity player);

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

package de.teamlapen.vampirism.api.entity.factions;

import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;

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

    /**
     * @return The highest reachable lord level or 0 if no lord
     */
    int getHighestLordLevel();

    /**
     * @param level  lord level
     * @param female Female version
     * @return A text component representing the title of the player at the given lord level. empty if level==0
     */
    @Nonnull
    ITextComponent getLordTitle(int level, boolean female);

}

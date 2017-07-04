package de.teamlapen.vampirism.api.entity.player;

import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.factions.IFactionEntity;
import de.teamlapen.vampirism.api.entity.factions.IFactionPlayerHandler;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.skills.ISkillPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

import javax.annotation.Nullable;
import java.util.function.Predicate;

/**
 * Basic interface for all of Vampirism's player types (VampirePlayer, HunterPlayer, ...)
 * The child classes are used as capabilities and attached to the player
 * The player can have levels.
 * A player can only be part of one faction at once, this means only one IFactionPlayer capability belonging to a single player can have a level >0.
 * <p>
 * If you are writing an addon and not a standalone mod, consider extending VampirismPlayer instead of implementing this
 */
public interface IFactionPlayer<T extends IFactionPlayer> extends IFactionEntity, ISkillPlayer<T> {
    /**
     * Mostly relevant in the set level command
     * Vampirism's factions always return true here.
     * Can be used if another mod does not want that a player leaves it's faction via the command
     *
     * @return
     */
    boolean canLeaveFaction();

    /**
     * If not disguised this should return the ACTUAL FACTION of the player NOT NULL. Null represents neutral players
     *
     * @return The faction the player is disguised as.
     */
    @Nullable
    IFaction getDisguisedAs();

    /**
     * @return the faction this faction player belongs to
     */
    IPlayableFaction<T> getFaction();

    /**
     * Preferably implement this by calling {@link IFactionPlayerHandler#getCurrentLevel(IPlayableFaction)}
     *
     * @return 0 if the player is not part of this faction, something > 0 if the player is part of the faction.
     */
    int getLevel();

    /**
     * @return Max level this player type can reach
     */
    int getMaxLevel();

    /**
     * Careful this selects all {@link Entity}'s including etc Items
     *
     * @param otherFactionPlayers Whether other entities from the same faction that might be hostile should be included
     * @param ignoreDisguise      If disguised players should still be counted for their actual faction
     * @return A predicate that selects all non friendly entities
     */
    Predicate<Entity> getNonFriendlySelector(boolean otherFactionPlayers, boolean ignoreDisguise);

    EntityPlayer getRepresentingPlayer();

    /**
     * You can also use {@link IFactionPlayer#getDisguisedAs()} to get the faction the player looks like
     *
     * @return If the player is disguised.
     */
    boolean isDisguised();

    /**
     * Returns false for a null world
     *
     * @return if the player is in a remote world
     */
    boolean isRemote();

    /**
     * Is called when the players faction level changed.
     * Is called on world load.
     * Is called on client and server side.
     * Might be called with oldLevel=newLevel to reset things
     *
     * @param newLevel
     * @param oldLevel
     */
    void onLevelChanged(int newLevel, int oldLevel);

}

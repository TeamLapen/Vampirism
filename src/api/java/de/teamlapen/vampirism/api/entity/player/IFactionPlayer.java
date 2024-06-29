package de.teamlapen.vampirism.api.entity.player;

import de.teamlapen.vampirism.api.entity.factions.*;
import de.teamlapen.vampirism.api.extensions.IPlayer;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

/**
 * Basic interface for all of Vampirism's player types (VampirePlayer, HunterPlayer, ...)
 * The child classes are used as capabilities and attached to the player.
 * A player can have levels.
 * A player can only be part of one faction at once, this means only one IFactionPlayer capability belonging to a single player can have a level >0.
 * <p>
 * If you are writing an addon and not a standalone mod, consider extending FactionPlayerBase instead of implementing this
 */
public interface IFactionPlayer<T extends IFactionPlayer<T>> extends IFactionEntity, IPlayer {
    /**
     * Mostly relevant in the set level command
     * Vampirism's factions always return true here.
     * Can be used if another mod does not want that a player leaves its faction via the command
     */
    @SuppressWarnings("SameReturnValue")
    boolean canLeaveFaction();

    /**
     * If not disguised this should return the ACTUAL FACTION of the player NOT NULL. Null represents neutral players
     *
     * @return The faction the player is disguised as.
     */
    @Nullable
    @Deprecated(forRemoval = true)
    default IFaction<?> getDisguisedAs() {
        var holder = getDisguise().getViewedFaction(null);
        return holder == null ? null : holder.value();
    }

    IDisguise getDisguise();

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

    @Override
    @NotNull
    Holder<? extends IPlayableFaction<?>> getFaction();

    /**
     * Careful this selects all {@link LivingEntity}'s including etc. Items
     *
     * @param otherFactionPlayers Whether other entities from the same faction that might be hostile should be included
     * @param ignoreDisguise      If disguised players should still be counted for their actual faction
     * @return A predicate that selects all non-friendly entities
     */
    Predicate<LivingEntity> getNonFriendlySelector(boolean otherFactionPlayers, boolean ignoreDisguise);

    /**
     * @deprecated use {@link #asEntity()} instead
     */
    @SuppressWarnings("DeprecatedIsStillUsed")
    @Deprecated
    Player getRepresentingPlayer();

    @Override
    default @NotNull Player asEntity() {
        return getRepresentingPlayer();
    }

    /**
     * You can also use {@link de.teamlapen.vampirism.api.entity.player.IFactionPlayer#getDisguise()} to get the faction the player looks like
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
     * Is called when the player's faction level changed.
     * Is called on world load.
     * Is called on client and server side.
     * Might be called with oldLevel=newLevel to reset things
     */
    void onLevelChanged(int newLevel, int oldLevel);

}

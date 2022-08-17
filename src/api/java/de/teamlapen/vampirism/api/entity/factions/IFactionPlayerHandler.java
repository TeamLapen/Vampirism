package de.teamlapen.vampirism.api.entity.factions;

import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.ILordPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Handles factions and levels for the player
 * Attached to all players as capability
 */
public interface IFactionPlayerHandler extends ILordPlayer {

    /**
     * Players can only join a faction if they are in no other.
     *
     * @return If the player can join the given faction
     */
    boolean canJoin(IPlayableFaction<?> faction);

    /**
     * Checks currents factions {@link IFactionPlayer#canLeaveFaction()}
     */
    boolean canLeaveFaction();

    /**
     * @return The currently active faction. Can be null
     */
    @Nullable
    IPlayableFaction<?> getCurrentFaction();

    /**
     * @return The currently active faction player. Can be null
     */
    @NotNull
    Optional<? extends IFactionPlayer<?>> getCurrentFactionPlayer();

    /**
     * If no faction is active this returns 0.
     * Prefer using {@link IFactionPlayer#getLevel()} unless you are checking your own faction, since other factions might handle things differently
     *
     * @return the level of the currently active faction
     */
    int getCurrentLevel();

    /**
     * Makes some things easier.
     * Prefer using {@link IFactionPlayer#getLevel()} unless you are checking your own faction, since other factions might handle things differently
     *
     * @return If the faction is active: The faction level, otherwise 0
     */
    int getCurrentLevel(IPlayableFaction<?> f);

    /**
     * If not in faction returns 0f
     *
     * @return Level/MaxLevel. Between 0f and 1f.
     */
    float getCurrentLevelRelative();

    /**
     * @return The player represented by this handler
     */
    @NotNull
    Player getPlayer();

    /**
     * @return If the given faction is equal to the current one
     */
    boolean isInFaction(IFaction<?> f);

    /**
     * Join the given faction and set the faction level to 1.
     * Only successful if {@link IFactionPlayerHandler#canJoin(IPlayableFaction)}
     */
    void joinFaction(@NotNull IPlayableFaction<?> faction);

    /**
     * Should be called if the entity attacked.
     * If this returns false the attack should be canceled
     *
     * @return If false the attack should be canceled
     */
    boolean onEntityAttacked(DamageSource src, float amt);

    /**
     * Set the players faction and it's level. Only use this if you are sure that you want to override the previous faction.
     *
     * @return If successful
     */
    boolean setFactionAndLevel(@Nullable IPlayableFaction<?> faction, int level);

    /**
     * Set the level for a faction. Only works if the player already is in the given faction.
     * Use {@link IFactionPlayerHandler#joinFaction(IPlayableFaction)} to join a faction first or {@link IFactionPlayerHandler#setFactionAndLevel(IPlayableFaction, int)} if you are sure what you do
     *
     * @return If successful
     */
    boolean setFactionLevel(@NotNull IPlayableFaction<?> faction, int level);

    /**
     * Set the players lord level.
     * Checks if player is in faction and at faction max level and if level is lower than max lord level
     *
     * @return if successful
     */
    boolean setLordLevel(int level);

    /**
     * Leave the current faction (if in any) by setting current faction to null and level to 0.
     * @param die Whether to attack the player with deadly damage
     */
    void leaveFaction(boolean die);
}

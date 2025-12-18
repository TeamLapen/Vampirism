package de.teamlapen.factions.api.factions;

import de.teamlapen.factions.api.factions.actions.IActionHandler;
import de.teamlapen.factions.api.factions.lord.ILordPlayer;
import de.teamlapen.factions.api.factions.refinements.IRefinementHandler;
import de.teamlapen.factions.api.factions.refinements.IRefinementPlayer;
import de.teamlapen.factions.api.factions.skills.ISkillHandler;
import de.teamlapen.factions.api.factions.skills.ISkillPlayer;
import de.teamlapen.factions.api.factions.tasks.ITaskManager;
import de.teamlapen.factions.api.factions.tasks.ITaskPlayer;
import de.teamlapen.factions.api.registries.factions.DeferredFaction;
import de.teamlapen.factions.api.world.entities.extensions.IPlayer;
import de.teamlapen.factions.api.world.entities.player.IFactionPlayer;
import net.minecraft.core.Holder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;

import java.util.Optional;

/**
 * Handles factions and levels for the player
 * Attached to all players as capability
 */
public interface IFactionPlayerHandler extends IPlayer {

    /**
     * Players can only join a faction if they are in no other.
     *
     * @return If the player can join the given faction
     */
    boolean canJoin(Holder<? extends IPlayableFaction<?>> faction);

    /**
     * Checks currents factions {@link IFactionPlayer#canLeaveFaction()}
     */
    boolean canLeaveFaction();

    /**
     * @return The currently active faction. Can be null
     */
    Holder<? extends IPlayableFaction<?>> getFaction();

    /**
     * @return The currently active faction player
     */
    <T extends IFactionPlayer<T>> T factionPlayer();

    /**
     * returns the faction player for the given faction.
     *
     * @param faction the faction
     * @return the faction player or empty if the player is not in the faction
     * @apiNote The result is only not empty if the player is in the faction.
     */
    <T extends IFactionPlayer<T>> Optional<T> factionPlayer(Holder<IFaction<T>> faction);

    default <T extends IFactionPlayer<T>> Optional<T> factionPlayer(DeferredFaction<T, ? extends IFaction<T>> faction) {
        //noinspection unchecked
        return factionPlayer((Holder<IFaction<T>>) faction);
    }

    /**
     * Returns the currently active faction player.
     *
     * @apiNote Prefer to call interface-specific methods for granular access.
     */
    <T extends IFactionPlayer<T>> Optional<T> getCurrentFactionPlayer();

    <T extends ISkillPlayer<T>> Optional<T> getCurrentSkillPlayer();

    <T extends IRefinementPlayer<T>> Optional<T> getCurrentRefinementPlayer();

    <T extends ISkillPlayer<T>> Optional<ISkillHandler<T>> getSkillHandler();

    <T extends ISkillPlayer<T>> Optional<IActionHandler<T>> getActionHandler();

    <T extends IRefinementPlayer<T>> Optional<IRefinementHandler<T>> getRefinementHandler();

    <T extends ITaskPlayer<T>> Optional<T> getTaskPlayer();

    <T extends ILordPlayer<T>> Optional<T> getLordPlayer();

    Optional<ITaskManager> getTaskManager();

    /**
     * If no faction is active this returns 0.
     * Prefer using {@link IFactionPlayer#getLevel()} unless you are checking your own faction, since other factions might handle things differently
     *
     * @return the level of the currently active faction
     */
    int getCurrentLevel();

    int getLordLevel();

    IPlayableFaction.TitleGender titleGender();

    /**
     * Makes some things easier.
     * Prefer using {@link IFactionPlayer#getLevel()} unless you are checking your own faction, since other factions might handle things differently
     *
     * @return If the faction is active: The faction level, otherwise 0
     */
    int getCurrentLevel(Holder<? extends IFaction<?>> f);

    /**
     * If not in faction returns 0f
     *
     * @return Level/MaxLevel. Between 0f and 1f.
     */
    float getCurrentLevelRelative();

    <T extends IFaction<?>> boolean isInFaction(Holder<T> f);

    <T extends IFaction<?>> boolean isInFaction(TagKey<T> f);

    /**
     * Join the given faction and set the faction level to 1.
     * Only successful if {@link IFactionPlayerHandler#canJoin(net.minecraft.core.Holder)}
     */
    void joinFaction(Holder<? extends IPlayableFaction<?>> faction);

    /**
     * Should be called if the entity attacked.
     * If this returns false the attack should be canceled
     *
     * @return If false the attack should be canceled
     */
    boolean onEntityAttacked(DamageSource src, float amt);

    boolean setFaction(LevelingChange param);

    default boolean setFaction(LevelingChange.Builder param) {
        return setFaction(param.build());
    }

    /**
     * Leave the current faction (if in any) by setting current faction to null and level to 0.
     *
     * @param die Whether to attack the player with deadly damage
     */
    void leaveFaction(boolean die);

    /**
     * Checks which skill trees are unlocked.
     * It locks and unlocks the skill trees accordingly.
     * <p>
     * It is called when the player level or lord level changes as well as when the player respawns. But it can be called at any time.
     */
    void checkSkillTreeLocks();


}

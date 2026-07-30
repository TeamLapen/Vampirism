package de.teamlapen.faction.api.factions;

import de.teamlapen.faction.api.factions.actions.IActionHandler;
import de.teamlapen.faction.api.factions.lord.ILordPlayer;
import de.teamlapen.faction.api.factions.refinements.IRefinementHandler;
import de.teamlapen.faction.api.factions.skills.ISkillHandler;
import de.teamlapen.faction.api.factions.skills.ISkillPlayer;
import de.teamlapen.faction.api.factions.tasks.ITaskManager;
import de.teamlapen.faction.api.registries.factions.DeferredFaction;
import de.teamlapen.faction.api.world.entities.extensions.IPlayer;
import de.teamlapen.faction.api.world.entities.player.IFactionPlayer;
import net.minecraft.core.Holder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.neoforged.neoforge.common.MutableDataComponentHolder;

import java.util.Optional;

/**
 * Handles factions and levels for the player
 * Attached to all players as capability
 */
public interface IFactionPlayerHandler extends IPlayer, MutableDataComponentHolder, IFactionExtensionGetter {

    //<editor-fold desc="Faction access">

    /**
     * @return The currently active faction player
     */
    <T extends IFactionPlayer<T>> T factionPlayer();

    /**
     * @return The currently active faction
     */
    Holder<? extends IPlayableFaction<?>> getFaction();

    <T extends IFaction<?>> boolean isInFaction(Holder<T> f);

    <T extends IFaction<?>> boolean isInFaction(TagKey<T> f);

    default boolean setFaction(LevelingChange.Builder param) {
        return setFaction(param.build());
    }

    boolean setFaction(LevelingChange param);

    /**
     * Checks currents factions {@link IFactionPlayer#canLeaveFaction()}
     */
    boolean canLeaveFaction();

    /**
     * Leave the current faction (if in any) by setting current faction to null and level to 0.
     *
     * @param die Whether to attack the player with deadly damage
     */
    void leaveFaction(boolean die);

    /**
     * Players can only join a faction if they are in no other.
     *
     * @return If the player can join the given faction
     */
    boolean canJoin(Holder<? extends IPlayableFaction<?>> faction);

    /**
     * Join the given faction and set the faction level to 1.
     * Only successful if {@link IFactionPlayerHandler#canJoin(net.minecraft.core.Holder)} is true
     */
    void joinFaction(Holder<? extends IPlayableFaction<?>> faction);

    //</editor-fold>

    //<editor-fold desc="Capability Access">

    /**
     * returns the faction player for the given faction.
     *
     * @param faction the faction
     * @return the faction player or empty if the player is not in the faction
     * @apiNote The result is only not empty if the player is in the faction.
     */
    <T extends IFactionPlayer<T>> Optional<T> factionPlayer(Holder<IFaction<T>> faction);

    /**
     * {@link de.teamlapen.faction.api.registries.factions.DeferredFaction} helper for {@link #factionPlayer(net.minecraft.core.Holder)}
     */
    default <T extends IFactionPlayer<T>> Optional<T> factionPlayer(DeferredFaction<T, ? extends IFaction<T>> faction) {
        //noinspection unchecked
        return factionPlayer((Holder<IFaction<T>>) faction);
    }

    <T extends ISkillPlayer<T>> Optional<T> getCurrentSkillPlayer();

    <T extends ISkillPlayer<T>> Optional<ISkillHandler<T>> getSkillHandler();

    <T extends ISkillPlayer<T>> Optional<IActionHandler<T>> getActionHandler();

    //</editor-fold>

    //<editor-fold desc="Faction Properties">

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
    int getCurrentLevel(Holder<? extends IFaction<?>> f);

    /**
     * If not in faction returns 0f
     *
     * @return Level/MaxLevel. Between 0f and 1f.
     */
    float getCurrentLevelRelative();

    //</editor-fold>

    /**
     * Should be called if the entity attacked.
     * If this returns false the attack should be canceled
     *
     * @return If false the attack should be canceled
     */
    boolean onEntityAttacked(DamageSource src, float amt);


    /**
     * Checks which skill trees are unlocked.
     * It locks and unlocks the skill trees accordingly.
     * <p>
     * It is called when the player level or lord level changes as well as when the player respawns. But it can be called at any time.
     */
    void checkSkillTreeLocks();

}

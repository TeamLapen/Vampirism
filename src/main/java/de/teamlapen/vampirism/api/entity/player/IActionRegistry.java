package de.teamlapen.vampirism.api.entity.player;

import com.google.common.collect.ImmutableBiMap;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;

/**
 * Registry for vampire actions.
 * Never use the Integer id's here, they are only intended to be used for sync and to update timers
 */
public interface IActionRegistry {

    /**
     * @param faction
     * @return The amount of actions that are registered for the given faction
     */
    int getActionCount(IPlayableFaction faction);

    /**
     * @param key
     * @return the action that is registered with the given key for the given faction
     */
    IAction getActionFromKey(IPlayableFaction faction, String key);

    /**
     * @param faction
     * @return A immutable map with key/action entries for the given faction
     */
    ImmutableBiMap getActionMapForFaction(IPlayableFaction faction);

    /**
     * @param action
     * @return the key which maps to the given action
     */
    String getKeyFromAction(IAction action);

    /**
     * Register a action during init
     *
     * @param action
     * @return The same action
     */
    <T extends IAction> T registerAction(T action, String key);
}

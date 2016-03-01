package de.teamlapen.vampirism.api.entity.player.vampire;

import java.util.List;

/**
 * Registry for vampire actions.
 * Never use the Integer id's here, they are only intended to be used for sync and to update timers
 */
public interface IActionRegistry {
    int getActionCount();

    /**
     * @param key
     * @return the action that is registered with the given key
     */
    IVampireAction getActionFromKey(String key);

    /**
     * @param player
     * @return A list of all actions the player can currently use
     */
    List<IVampireAction> getAvailableActions(IVampirePlayer player);

    /**
     * @param action
     * @return the key which maps to the given action
     */
    String getKeyFromAction(IVampireAction action);

    /**
     * Register a action
     * Preferably during init
     *
     * @param skill
     * @return The same action
     */
    <T extends IVampireAction> T registerAction(T skill, String key);
}

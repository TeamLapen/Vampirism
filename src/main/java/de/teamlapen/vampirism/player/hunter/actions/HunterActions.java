package de.teamlapen.vampirism.player.hunter.actions;

import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.player.actions.IActionRegistry;

/**
 * Registers and holds all skills for hunter players
 */
public class HunterActions {
    public static DisguiseHunterAction disguiseAction;

    public static void registerDefaultActions() {
        IActionRegistry registry = VampirismAPI.actionRegistry();
        disguiseAction = registry.registerAction(new DisguiseHunterAction(), "disguise");
    }
}

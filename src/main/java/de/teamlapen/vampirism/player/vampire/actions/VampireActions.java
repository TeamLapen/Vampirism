package de.teamlapen.vampirism.player.vampire.actions;

import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.player.actions.IActionRegistry;

/**
 * Registers and holds all skills for vampire player
 */
public class VampireActions {
    public static FreezeVampireAction freezeAction;
    public static InvisibilityVampireAction invisibilityAction;
    public static RegenVampireAction regenAction;
    public static TeleportVampireAction teleportAction;
    public static VampireRageVampireAction rageAction;
    public static BatVampireAction batAction;
    public static SummonBatVampireAction summonBatAction;
    public static DisguiseVampireAction disguiseAction;

    public static void registerDefaultActions() {
        IActionRegistry registry = VampirismAPI.actionRegistry();
        freezeAction = registry.registerAction(new FreezeVampireAction(), "freeze");
        invisibilityAction = registry.registerAction(new InvisibilityVampireAction(), "invisible");
        regenAction = registry.registerAction(new RegenVampireAction(), "regen");
        teleportAction = registry.registerAction(new TeleportVampireAction(), "teleport");
        rageAction = registry.registerAction(new VampireRageVampireAction(), "rage");
        batAction = registry.registerAction(new BatVampireAction(), "bat");
        disguiseAction = registry.registerAction(new DisguiseVampireAction(), "disguise");
        summonBatAction = registry.registerAction(new SummonBatVampireAction(), "summonbat");
    }
}

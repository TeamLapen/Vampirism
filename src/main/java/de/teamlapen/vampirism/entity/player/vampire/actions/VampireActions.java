package de.teamlapen.vampirism.entity.player.vampire.actions;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import de.teamlapen.vampirism.api.entity.player.actions.ILastingAction;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.api.registries.DeferredAction;
import de.teamlapen.vampirism.api.registries.DeferredActionRegister;
import net.neoforged.bus.api.IEventBus;
import org.jetbrains.annotations.ApiStatus;

/**
 * Registers and holds all skills for vampire player
 */
public class VampireActions {
    public static final DeferredActionRegister<IVampirePlayer> ACTIONS = DeferredActionRegister.create(REFERENCE.MODID);

    public static final DeferredAction<IVampirePlayer, ILastingAction<IVampirePlayer>, BatVampireAction> BAT = ACTIONS.registerAction("bat", BatVampireAction::new);
    public static final DeferredAction<IVampirePlayer, IAction<IVampirePlayer>, DarkBloodProjectileAction> DARK_BLOOD_PROJECTILE = ACTIONS.registerAction("dark_blood_projectile", DarkBloodProjectileAction::new);
    public static final DeferredAction<IVampirePlayer, ILastingAction<IVampirePlayer>, DisguiseVampireAction> DISGUISE_VAMPIRE = ACTIONS.registerAction("disguise_vampire", DisguiseVampireAction::new);
    public static final DeferredAction<IVampirePlayer, IAction<IVampirePlayer>, FreezeVampireAction> FREEZE = ACTIONS.registerAction("freeze", FreezeVampireAction::new);
    public static final DeferredAction<IVampirePlayer, ILastingAction<IVampirePlayer>, HalfInvulnerableAction> HALF_INVULNERABLE = ACTIONS.registerAction("half_invulnerable", HalfInvulnerableAction::new);
    public static final DeferredAction<IVampirePlayer, ILastingAction<IVampirePlayer>, RegenVampireAction> REGEN = ACTIONS.registerAction("regen", RegenVampireAction::new);
    public static final DeferredAction<IVampirePlayer, ILastingAction<IVampirePlayer>, SunscreenVampireAction> SUNSCREEN = ACTIONS.registerAction("sunscreen", SunscreenVampireAction::new);
    public static final DeferredAction<IVampirePlayer, IAction<IVampirePlayer>, SummonBatVampireAction> SUMMON_BAT = ACTIONS.registerAction("summon_bat", SummonBatVampireAction::new);
    public static final DeferredAction<IVampirePlayer, IAction<IVampirePlayer>, TeleportVampireAction> TELEPORT = ACTIONS.registerAction("teleport", TeleportVampireAction::new);
    public static final DeferredAction<IVampirePlayer, ILastingAction<IVampirePlayer>, InvisibilityVampireAction> VAMPIRE_INVISIBILITY = ACTIONS.registerAction("vampire_invisibility", InvisibilityVampireAction::new);
    public static final DeferredAction<IVampirePlayer, ILastingAction<IVampirePlayer>, RageVampireAction> VAMPIRE_RAGE = ACTIONS.registerAction("vampire_rage", RageVampireAction::new);
    public static final DeferredAction<IVampirePlayer, IAction<IVampirePlayer>, HissingAction> HISSING = ACTIONS.registerAction("hissing", HissingAction::new);
    public static final DeferredAction<IVampirePlayer, IAction<IVampirePlayer>, InfectAction> INFECT = ACTIONS.registerAction("infect", InfectAction::new);
    public static final DeferredAction<IVampirePlayer, ILastingAction<IVampirePlayer>, DarkStalker> DARK_STALKER = ACTIONS.registerAction("dark_stalker", DarkStalker::new);

    @ApiStatus.Internal
    public static void register(IEventBus bus) {
        ACTIONS.register(bus);
    }
}

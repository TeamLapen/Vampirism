package de.teamlapen.vampirism.common.world.entity.player.vampire.actions;

import de.teamlapen.faction.api.factions.actions.IAction;
import de.teamlapen.faction.api.factions.actions.ILastingAction;
import de.teamlapen.faction.api.registries.actions.DeferredAction;
import de.teamlapen.faction.api.registries.actions.DeferredActionRegister;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.world.entity.player.vampire.IVampirePlayer;
import net.neoforged.bus.api.IEventBus;
import org.jetbrains.annotations.ApiStatus;

/**
 * Registers and holds all skills for vampire player
 */
public class VampireActions {
    public static final DeferredActionRegister<IVampirePlayer> ACTIONS = DeferredActionRegister.create(REFERENCE.MODID);

    public static final DeferredAction<IVampirePlayer, ILastingAction<IVampirePlayer>, RegenerationAction> REGENERATION = ACTIONS.registerAction("regeneration", RegenerationAction::new);
    public static final DeferredAction<IVampirePlayer, ILastingAction<IVampirePlayer>, BatFormAction> BAT_FORM = ACTIONS.registerAction("bat_form", BatFormAction::new);
    public static final DeferredAction<IVampirePlayer, IAction<IVampirePlayer>, InfectAction> INFECT = ACTIONS.registerAction("infect", InfectAction::new);

    public static final DeferredAction<IVampirePlayer, ILastingAction<IVampirePlayer>, VampireRageAction> VAMPIRE_RAGE = ACTIONS.registerAction("vampire_rage", VampireRageAction::new);
    public static final DeferredAction<IVampirePlayer, IAction<IVampirePlayer>, DarkBloodProjectileAction> DARK_BLOOD_PROJECTILE = ACTIONS.registerAction("dark_blood_projectile", DarkBloodProjectileAction::new);
    public static final DeferredAction<IVampirePlayer, IAction<IVampirePlayer>, FreezeAction> FREEZE = ACTIONS.registerAction("freeze", FreezeAction::new);

    public static final DeferredAction<IVampirePlayer, ILastingAction<IVampirePlayer>, SunscreenAction> SUNSCREEN = ACTIONS.registerAction("sunscreen", SunscreenAction::new);
    public static final DeferredAction<IVampirePlayer, IAction<IVampirePlayer>, TeleportAction> TELEPORT = ACTIONS.registerAction("teleport", TeleportAction::new);

    public static final DeferredAction<IVampirePlayer, IAction<IVampirePlayer>, SummonBatsAction> SUMMON_BATS = ACTIONS.registerAction("summon_bats", SummonBatsAction::new);
    public static final DeferredAction<IVampirePlayer, ILastingAction<IVampirePlayer>, DisguiseAction> DISGUISE = ACTIONS.registerAction("disguise", DisguiseAction::new);
    public static final DeferredAction<IVampirePlayer, ILastingAction<IVampirePlayer>, DamageLimiterAction> DAMAGE_LIMITER = ACTIONS.registerAction("damage_limiter", DamageLimiterAction::new);
    public static final DeferredAction<IVampirePlayer, ILastingAction<IVampirePlayer>, InvisibilityAction> INVISIBILITY = ACTIONS.registerAction("invisibility", InvisibilityAction::new);
    public static final DeferredAction<IVampirePlayer, IAction<IVampirePlayer>, HissingAction> HISSING = ACTIONS.registerAction("hissing", HissingAction::new);
    public static final DeferredAction<IVampirePlayer, ILastingAction<IVampirePlayer>, DarkStalkerAction> DARK_STALKER = ACTIONS.registerAction("dark_stalker", DarkStalkerAction::new);
    public static final DeferredAction<IVampirePlayer, ILastingAction<IVampirePlayer> ,JumpBoostAction> JUMP_BOOST = ACTIONS.registerAction("jump_boost", JumpBoostAction::new);
    public static final DeferredAction<IVampirePlayer, IAction<IVampirePlayer>, MistAction> MIST = ACTIONS.registerAction("mist", MistAction::new);
    public static final DeferredAction<IVampirePlayer, IAction<IVampirePlayer>, AuraOfDarknessAction> AURA_OF_DARKNESS = ACTIONS.registerAction("aura_of_darkness", AuraOfDarknessAction::new);

    @ApiStatus.Internal
    public static void register(IEventBus bus) {
        ACTIONS.register(bus);
    }
}

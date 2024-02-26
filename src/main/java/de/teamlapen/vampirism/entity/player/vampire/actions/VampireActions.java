package de.teamlapen.vampirism.entity.player.vampire.actions;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.VampirismRegistries;
import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.ApiStatus;

/**
 * Registers and holds all skills for vampire player
 */
public class VampireActions {
    public static final DeferredRegister<IAction<?>> ACTIONS = DeferredRegister.create(VampirismRegistries.Keys.ACTION, REFERENCE.MODID);

    public static final DeferredHolder<IAction<?>, BatVampireAction> BAT = ACTIONS.register("bat", BatVampireAction::new);
    public static final DeferredHolder<IAction<?>, DarkBloodProjectileAction> DARK_BLOOD_PROJECTILE = ACTIONS.register("dark_blood_projectile", DarkBloodProjectileAction::new);
    public static final DeferredHolder<IAction<?>, DisguiseVampireAction> DISGUISE_VAMPIRE = ACTIONS.register("disguise_vampire", DisguiseVampireAction::new);
    public static final DeferredHolder<IAction<?>, FreezeVampireAction> FREEZE = ACTIONS.register("freeze", FreezeVampireAction::new);
    public static final DeferredHolder<IAction<?>, HalfInvulnerableAction> HALF_INVULNERABLE = ACTIONS.register("half_invulnerable", HalfInvulnerableAction::new);
    public static final DeferredHolder<IAction<?>, RegenVampireAction> REGEN = ACTIONS.register("regen", RegenVampireAction::new);
    public static final DeferredHolder<IAction<?>, SunscreenVampireAction> SUNSCREEN = ACTIONS.register("sunscreen", SunscreenVampireAction::new);
    public static final DeferredHolder<IAction<?>, SummonBatVampireAction> SUMMON_BAT = ACTIONS.register("summon_bat", SummonBatVampireAction::new);
    public static final DeferredHolder<IAction<?>, TeleportVampireAction> TELEPORT = ACTIONS.register("teleport", TeleportVampireAction::new);
    public static final DeferredHolder<IAction<?>, InvisibilityVampireAction> VAMPIRE_INVISIBILITY = ACTIONS.register("vampire_invisibility", InvisibilityVampireAction::new);
    public static final DeferredHolder<IAction<?>, RageVampireAction> VAMPIRE_RAGE = ACTIONS.register("vampire_rage", RageVampireAction::new);
    public static final DeferredHolder<IAction<?>, HissingAction> HISSING = ACTIONS.register("hissing", HissingAction::new);
    public static final DeferredHolder<IAction<?>, InfectAction> INFECT = ACTIONS.register("infect", InfectAction::new);

    @ApiStatus.Internal
    public static void register(IEventBus bus) {
        ACTIONS.register(bus);
    }
}

package de.teamlapen.vampirism.player.vampire.actions;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import de.teamlapen.vampirism.core.ModRegistries;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

/**
 * Registers and holds all skills for vampire player
 */
public class VampireActions {
    public static final DeferredRegister<IAction<?>> ACTIONS = DeferredRegister.create(ModRegistries.ACTIONS_ID, REFERENCE.MODID);
    
    public static final RegistryObject<BatVampireAction> bat = ACTIONS.register("bat", BatVampireAction::new);
    public static final RegistryObject<DarkBloodProjectileAction> dark_blood_projectile = ACTIONS.register("dark_blood_projectile", DarkBloodProjectileAction::new);
    public static final RegistryObject<DisguiseVampireAction> disguise_vampire = ACTIONS.register("disguise_vampire", DisguiseVampireAction::new);
    public static final RegistryObject<FreezeVampireAction> freeze = ACTIONS.register("freeze", FreezeVampireAction::new);
    public static final RegistryObject<HalfInvulnerableAction> half_invulnerable = ACTIONS.register("half_invulnerable", HalfInvulnerableAction::new);
    public static final RegistryObject<RegenVampireAction> regen = ACTIONS.register("regen", RegenVampireAction::new);
    public static final RegistryObject<SunscreenVampireAction> sunscreen = ACTIONS.register("sunscreen", SunscreenVampireAction::new);
    public static final RegistryObject<SummonBatVampireAction> summon_bat = ACTIONS.register("summon_bat", SummonBatVampireAction::new);
    public static final RegistryObject<TeleportVampireAction> teleport = ACTIONS.register("teleport", TeleportVampireAction::new);
    public static final RegistryObject<InvisibilityVampireAction> vampire_invisibility = ACTIONS.register("vampire_invisibility", InvisibilityVampireAction::new);
    public static final RegistryObject<RageVampireAction> vampire_rage = ACTIONS.register("vampire_rage", RageVampireAction::new);
    public static final RegistryObject<HissingAction> hissing = ACTIONS.register("hissing", HissingAction::new);

    public static void registerDefaultActions(IEventBus bus) {
        ACTIONS.register(bus);
    }
}

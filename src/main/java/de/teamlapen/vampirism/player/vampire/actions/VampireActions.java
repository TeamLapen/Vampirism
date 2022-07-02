package de.teamlapen.vampirism.player.vampire.actions;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismRegistries;
import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.player.actions.AttackSpeedLordAction;
import de.teamlapen.vampirism.player.actions.SpeedLordAction;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

/**
 * Registers and holds all skills for vampire player
 */
public class VampireActions {
    public static final DeferredRegister<IAction<?>> ACTIONS = DeferredRegister.create(VampirismRegistries.ACTIONS_ID, REFERENCE.MODID);

    public static final RegistryObject<BatVampireAction> BAT = ACTIONS.register("bat", BatVampireAction::new);
    public static final RegistryObject<DarkBloodProjectileAction> DARK_BLOOD_PROJECTILE = ACTIONS.register("dark_blood_projectile", DarkBloodProjectileAction::new);
    public static final RegistryObject<DisguiseVampireAction> DISGUISE_VAMPIRE = ACTIONS.register("disguise_vampire", DisguiseVampireAction::new);
    public static final RegistryObject<FreezeVampireAction> FREEZE = ACTIONS.register("freeze", FreezeVampireAction::new);
    public static final RegistryObject<HalfInvulnerableAction> HALF_INVULNERABLE = ACTIONS.register("half_invulnerable", HalfInvulnerableAction::new);
    public static final RegistryObject<RegenVampireAction> REGEN = ACTIONS.register("regen", RegenVampireAction::new);
    public static final RegistryObject<SunscreenVampireAction> SUNSCREEN = ACTIONS.register("sunscreen", SunscreenVampireAction::new);
    public static final RegistryObject<SummonBatVampireAction> SUMMON_BAT = ACTIONS.register("summon_bat", SummonBatVampireAction::new);
    public static final RegistryObject<TeleportVampireAction> TELEPORT = ACTIONS.register("teleport", TeleportVampireAction::new);
    public static final RegistryObject<InvisibilityVampireAction> VAMPIRE_INVISIBILITY = ACTIONS.register("vampire_invisibility", InvisibilityVampireAction::new);
    public static final RegistryObject<RageVampireAction> VAMPIRE_RAGE = ACTIONS.register("vampire_rage", RageVampireAction::new);
    public static final RegistryObject<HissingAction> HISSING = ACTIONS.register("hissing", HissingAction::new);
    public static final RegistryObject<InfectAction> INFECT = ACTIONS.register("infect", InfectAction::new);
    public static final RegistryObject<SpeedLordAction<IVampirePlayer>> VAMPIRE_LORD_SPEED = ACTIONS.register("vampire_lord_speed", () -> new SpeedLordAction<>(VReference.VAMPIRE_FACTION));
    public static final RegistryObject<AttackSpeedLordAction<IVampirePlayer>> VAMPIRE_LORD_ATTACK_SPEED = ACTIONS.register("vampire_lord_attack_speed", () -> new AttackSpeedLordAction<>(VReference.VAMPIRE_FACTION));

    public static void registerDefaultActions(IEventBus bus) {
        ACTIONS.register(bus);
    }
}

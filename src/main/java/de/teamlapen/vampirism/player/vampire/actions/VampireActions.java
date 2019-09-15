package de.teamlapen.vampirism.player.vampire.actions;

import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

import static de.teamlapen.lib.lib.util.UtilLib.getNull;

/**
 * Registers and holds all skills for vampire player
 */
@ObjectHolder(REFERENCE.MODID)
public class VampireActions {
    public static final BatVampireAction bat = getNull();
    public static final DarkBloodProjectileAction dark_blood_projectile = getNull();
    public static final DisguiseVampireAction disguise_vampire = getNull();
    public static final FreezeVampireAction freeze = getNull();
    public static final HalfInvulnerableAction half_invulnerable = getNull();
    public static final RegenVampireAction regen = getNull();
    public static final SunscreenVampireAction sunscreen = getNull();
    public static final SummonBatVampireAction summon_bat = getNull();
    public static final TeleportVampireAction teleport = getNull();
    public static final InvisibilityVampireAction vampire_invisibility = getNull();
    public static final RageVampireAction vampire_rage = getNull();

    public static void registerDefaultActions(IForgeRegistry<IAction> registry) {
        registry.register(new BatVampireAction().setRegistryName(REFERENCE.MODID, "bat"));
        registry.register(new DarkBloodProjectileAction().setRegistryName(REFERENCE.MODID, "dark_blood_projectile"));
        registry.register(new DisguiseVampireAction().setRegistryName(REFERENCE.MODID, "disguise_vampire"));
        registry.register(new FreezeVampireAction().setRegistryName(REFERENCE.MODID, "freeze"));
        registry.register(new HalfInvulnerableAction().setRegistryName(REFERENCE.MODID, "half_invulnerable"));
        registry.register(new RegenVampireAction().setRegistryName(REFERENCE.MODID, "regen"));
        registry.register(new SunscreenVampireAction().setRegistryName(REFERENCE.MODID, "sunscreen"));
        registry.register(new SummonBatVampireAction().setRegistryName(REFERENCE.MODID, "summon_bat"));
        registry.register(new TeleportVampireAction().setRegistryName(REFERENCE.MODID, "teleport"));
        registry.register(new InvisibilityVampireAction().setRegistryName(REFERENCE.MODID, "vampire_invisibility"));
        registry.register(new RageVampireAction().setRegistryName(REFERENCE.MODID, "vampire_rage"));
    }
}

package de.teamlapen.vampirism.player.vampire.actions;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

/**
 * Registers and holds all skills for vampire player
 */
@ObjectHolder(REFERENCE.MODID)
public class VampireActions {
    public static final FreezeVampireAction freeze = UtilLib.getNull();
    public static final InvisibilityVampireAction vampire_invisibility = UtilLib.getNull();
    public static final RegenVampireAction regen = UtilLib.getNull();
    public static final TeleportVampireAction teleport = UtilLib.getNull();
    public static final RageVampireAction vampire_rage = UtilLib.getNull();
    public static final BatVampireAction bat = UtilLib.getNull();
    public static final SummonBatVampireAction summon_bat = UtilLib.getNull();
    public static final DisguiseVampireAction disguise_vampire = UtilLib.getNull();
    public static final SunscreenVampireAction sunscreen = UtilLib.getNull();
    public static final DarkBloodProjectileAction dark_blood_projectile = UtilLib.getNull();
    public static final HalfInvulnerableAction half_invulnerable = UtilLib.getNull();

    public static void registerDefaultActions(IForgeRegistry<IAction> registry) {
        registry.register(new FreezeVampireAction().setRegistryName(REFERENCE.MODID, "freeze"));
        registry.register(new InvisibilityVampireAction().setRegistryName(REFERENCE.MODID, "vampire_invisibility"));
        registry.register(new RegenVampireAction().setRegistryName(REFERENCE.MODID, "regen"));
        registry.register(new TeleportVampireAction().setRegistryName(REFERENCE.MODID, "teleport"));
        registry.register(new RageVampireAction().setRegistryName(REFERENCE.MODID, "vampire_rage"));
        registry.register(new BatVampireAction().setRegistryName(REFERENCE.MODID, "bat"));
        registry.register(new DisguiseVampireAction().setRegistryName(REFERENCE.MODID, "disguise_vampire"));
        registry.register(new SummonBatVampireAction().setRegistryName(REFERENCE.MODID, "summon_bat"));
        registry.register(new SunscreenVampireAction().setRegistryName(REFERENCE.MODID, "sunscreen"));
        registry.register(new DarkBloodProjectileAction().setRegistryName(REFERENCE.MODID, "dark_blood_projectile"));
        registry.register(new HalfInvulnerableAction().setRegistryName(REFERENCE.MODID, "half_invulnerable"));
    }
}

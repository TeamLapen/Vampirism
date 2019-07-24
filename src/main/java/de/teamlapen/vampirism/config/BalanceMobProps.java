package de.teamlapen.vampirism.config;

import de.teamlapen.lib.lib.config.BalanceValues;
import de.teamlapen.lib.lib.config.DefaultDouble;
import de.teamlapen.lib.lib.config.DefaultInt;
import de.teamlapen.vampirism.VampirismMod;

import java.io.File;

/**
 * Balance values for Vampirism's mobs
 */
public class BalanceMobProps extends BalanceValues {

    @DefaultDouble(value = 1.0, minValue = 0.0, maxValue = 1000, name = "vampire_mob_garlic_damage", comment = "UNUSED")
    public double VAMPIRE_MOB_GARLIC_DAMAGE;
    @DefaultInt(value = 600, name = "blinding_bat_live_spawn", minValue = 1, comment = "Livespan in ticks")
    public int BLINDING_BAT_LIVE_SPAWN;
    @DefaultInt(value = 40, name = "blinding_bat_effect_duration", minValue = 1, comment = "Blinding duration in ticks")
    public int BLINDING_BAT_EFFECT_DURATION;
    @DefaultInt(value = 30, minValue = 0, maxValue = 1000000, name = "blinding_bat_spawn_chance", comment = "e.g. Zombie spawn chance: 100")
    public int BLINDING_BAT_SPAWN_CHANCE;

    @DefaultInt(value = 75, minValue = 0, maxValue = 1000000, name = "dummy_creature_spawn_chance", comment = "e.g. Zombie spawn chance: 100")
    public int DUMMY_CREATURE_SPAWN_CHANCE;

    @DefaultDouble(value = 5D, name = "ghost_attack_damage", minValue = 0)
    public double GHOST_ATTACK_DAMAGE;
    @DefaultInt(value = 14, name = "ghost_follow_range", minValue = 1, maxValue = 32)
    public int GHOST_FOLLOW_RANGE;
    @DefaultDouble(value = 0.2D, name = "ghost_speed", minValue = 0, maxValue = 2)
    public double GHOST_SPEED;
    @DefaultInt(value = 30, name = "ghost_health", minValue = 0)
    public int GHOST_HEALTH;
    @DefaultInt(value = 50, minValue = 0, maxValue = 1000000, name = "ghost_spawn_chance", comment = "e.g. Zombie spawn chance: 100")
    public int GHOST_SPAWN_CHANCE;

    @DefaultInt(value = 1, minValue = 0, name = "converted_mob_default_dmg")
    public int CONVERTED_MOB_DEFAULT_DMG;

    @DefaultInt(value = 60, comment = "Duration of the sanguinare effect for mobs in seconds", name = "sanguinare_avg_duration", minValue = 1)
    public int SANGUINARE_AVG_DURATION;

    @DefaultInt(value = 20, name = "blood_regen_chance", comment = "Probability that a bitten creature will regen one blood. Is checked every 2 seconds with a 1/n chance")
    public int BLOOD_REGEN_CHANCE;

    @DefaultDouble(value = 30D, minValue = 10D, maxValue = 10000D, name = "hunter_max_health")
    public double VAMPIRE_HUNTER_MAX_HEALTH;
    @DefaultDouble(value = 4D, minValue = 0D, maxValue = 10000, name = "hunter_max_health_pl", comment = "Max health is increased by this for every level the hunter has")
    public double VAMPIRE_HUNTER_MAX_HEALTH_PL;
    @DefaultDouble(value = 3D, minValue = 0D, name = "hunter_attack_damage")
    public double VAMPIRE_HUNTER_ATTACK_DAMAGE;
    @DefaultDouble(value = 1.5D, minValue = 0D, name = "hunter_attack_damage_pl")
    public double VAMPIRE_HUNTER_ATTACK_DAMAGE_PL;
    @DefaultDouble(value = 0.28D, minValue = 0.1, maxValue = 2, name = "hunter_speed")
    public double VAMPIRE_HUNTER_SPEED;

    @DefaultDouble(value = 20D, minValue = 10D, maxValue = 10000D, name = "hunter_villager_max_health")
    public double HUNTER_VILLAGER_MAX_HEALTH;
    @DefaultDouble(value = 2D, minValue = 0D, name = "hunter_villager_attack_damage")
    public double HUNTER_VILLAGER_ATTACK_DAMAGE;

    @DefaultDouble(value = 60D, minValue = 10D, maxValue = 10000D, name = "advanced_hunter_max_health")
    public double ADVANCED_HUNTER_MAX_HEALTH;
    @DefaultDouble(value = 30D, minValue = 0D, maxValue = 10000, name = "advanced_hunter_max_health_pl", comment = "Max health is increased by this for every level the hunter has")
    public double ADVANCED_HUNTER_MAX_HEALTH_PL;
    @DefaultDouble(value = 5D, minValue = 0D, name = "advanced_hunter_attack_damage")
    public double ADVANCED_HUNTER_ATTACK_DAMAGE;
    @DefaultDouble(value = 3D, minValue = 0D, name = "advanced_hunter_attack_damage_pl")
    public double ADVANCED_HUNTER_ATTACK_DAMAGE_PL;
    @DefaultDouble(value = 0.285D, minValue = 0.1, maxValue = 2, name = "advanced_hunter_speed")
    public double ADVANCED_HUNTER_SPEED;

    @DefaultDouble(value = 60D, minValue = 10D, maxValue = 10000D, name = "advanced_vampire_max_health")
    public double ADVANCED_VAMPIRE_MAX_HEALTH;
    @DefaultDouble(value = 40D, minValue = 0D, maxValue = 10000, name = "advanced_vampire_max_health_pl", comment = "Max health is increased by this for every level the hunter has")
    public double ADVANCED_VAMPIRE_MAX_HEALTH_PL;
    @DefaultDouble(value = 7D, minValue = 0D, name = "advanced_vampire_attack_damage")
    public double ADVANCED_VAMPIRE_ATTACK_DAMAGE;
    @DefaultDouble(value = 5D, minValue = 0D, name = "advanced_vampire_attack_damage_pl")
    public double ADVANCED_VAMPIRE_ATTACK_DAMAGE_PL;
    @DefaultDouble(value = 0.285D, minValue = 0.1, maxValue = 2, name = "advanced_vampire_speed")
    public double ADVANCED_VAMPIRE_SPEED;
    @DefaultInt(value = 5, minValue = 0, maxValue = 100, comment = "Maximum number of vampires that will follow a advanced vampire")
    public int ADVANCED_VAMPIRE_MAX_FOLLOWER;
    @DefaultInt(value = 7, minValue = 0, maxValue = 1000000, name = "advanced_vampire_spawn_probe", comment = "e.g. Zombie spawn probe: 100")
    public int ADVANCED_VAMPIRE_SPAWN_PROBE;
    @DefaultDouble(value = 3, minValue = 0, comment = "Any fire damage that is received by a advanced vampire is multiplied by this")
    public double ADVANCED_VAMPIRE_FIRE_VULNERABILITY;


    @DefaultDouble(value = 30D, minValue = 10D, maxValue = 10000D, name = "vampire_max_health")
    public double VAMPIRE_MAX_HEALTH;
    @DefaultDouble(value = 3D, minValue = 0D, maxValue = 10000, name = "vampire_max_health_pl", comment = "Max health is increased by this for every level the vampire has")
    public double VAMPIRE_MAX_HEALTH_PL;
    @DefaultDouble(value = 3D, minValue = 0D, name = "vampire_attack_damage")
    public double VAMPIRE_ATTACK_DAMAGE;
    @DefaultDouble(value = 1D, minValue = 0D, name = "vampire_attack_damage_pl")
    public double VAMPIRE_ATTACK_DAMAGE_PL;
    @DefaultDouble(value = 0.3D, minValue = 0.1, maxValue = 2, name = "vampire_speed")
    public double VAMPIRE_SPEED;
    @DefaultInt(value = 75, minValue = 0, maxValue = 1000000, name = "vampire_spawn_chance", comment = "e.g. Zombie spawn chance: 100")
    public int VAMPIRE_SPAWN_CHANCE;
    @DefaultDouble(value = 2, minValue = 0, comment = "Any fire damage that is received by a basic vampire is multiplied by this")
    public double VAMPIRE_FIRE_VULNERABILITY;

    @DefaultInt(value = 2, minValue = 1, maxValue = 1000000, name = "vampire_bite_attack_chance", comment = "Chance that a vampire bites a player when attacking him (1/n)")
    public int VAMPIRE_BITE_ATTACK_CHANCE;

    @DefaultDouble(value = 7.0, minValue = 0.0, maxValue = 1000, name = "vampire_mob_sun_damage")
    public double VAMPIRE_MOB_SUN_DAMAGE;


    @DefaultDouble(value = 140.0D, minValue = 20.0D, maxValue = 300.0D, comment = "")
    public double VAMPIRE_BARON_MAX_HEALTH;
    @DefaultDouble(value = 6.0D, minValue = 1.0D, maxValue = 14.0D, comment = "")
    public double VAMPIRE_BARON_ATTACK_DAMAGE;
    @DefaultDouble(value = 0.34D, minValue = 0.1D, maxValue = 0.6D, comment = "")
    public double VAMPIRE_BARON_MOVEMENT_SPEED;
    @DefaultDouble(value = 1.20D, minValue = 1.0D, maxValue = 2.0D, comment = "For each higher level the stats are multiplied with this factor")
    public double VAMPIRE_BARON_IMPROVEMENT_PER_LEVEL;
    @DefaultDouble(value = 3, minValue = 0, comment = "Any fire damage that is received by a baron is multiplied by this")
    public double VAMPIRE_BARON_FIRE_VULNERABILITY;
    @DefaultInt(value = 10, minValue = 0, maxValue = 1000000, name = "vampire_baron_spawn_chance", comment = "e.g. Zombie spawn chance: 100")
    public int VAMPIRE_BARON_SPAWN_CHANCE;


    @DefaultDouble(value = 20.0D, minValue = 5.0D, maxValue = 200.0D, name = "Vampire Minion Max Health", comment = "")
    public double VAMPIRE_MINION_MAX_HEALTH;
    @DefaultDouble(value = 6.0D, minValue = 1.5D, maxValue = 20.0D, name = "Vampire Minion Attack Damage", comment = "")
    public double VAMPIRE_MINION_ATTACK_DAMAGE;
    @DefaultDouble(value = 0.25D, minValue = 0.1D, maxValue = 0.5D, name = "Vampire Minion Movement Speed", comment = "")
    public double VAMPIRE_MINION_MOVEMENT_SPEED;
    @DefaultInt(value = 5, minValue = -1, name = "Vampire Minion Regenerate Seconds", comment = "Regenerate 1 heart every n seconds. -1 to disable")
    public int VAMPIRE_MINION_REGENERATE_SECS;


    public BalanceMobProps(File directory) {
        super("mob_prop", directory);
    }

    @Override
    protected boolean shouldUseAlternate() {
        return VampirismMod.isRealism();
    }
}

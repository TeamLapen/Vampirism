package de.teamlapen.vampirism.config;

/**
 * Balance values for Vampirism's mobs
 */
public class BalanceMobProps {


    public static final BalanceMobProps mobProps = new BalanceMobProps();
    public final int BLINDING_BAT_LIVE_SPAWN = 600;
    //    @DefaultInt(value = 40, name = "blinding_bat_effect_duration", minValue = 1, comment = "Blinding duration in ticks")
    public final int BLINDING_BAT_EFFECT_DURATION = 80;

    //    @DefaultInt(value = 1, minValue = 0, name = "converted_mob_default_dmg")
    public final int CONVERTED_MOB_DEFAULT_DMG = 1;
    public final int CONVERTED_MOB_DEFAULT_SPEED = 1;
    public final int CONVERTED_MOB_DEFAULT_HEALTH = 1;
    public final int CONVERTED_MOB_DEFAULT_KNOCKBACK_RESISTANCE = 0;

    //    @DefaultInt(value = 60, comment = "Duration of the sanguinare effect for mobs in seconds", name = "sanguinare_avg_duration", minValue = 1)
    public final int SANGUINARE_AVG_DURATION = 30;

    //    @DefaultInt(value = 20, name = "blood_regen_chance", comment = "Probability that a bitten creature will regen one blood. Is checked every 2 seconds with a 1/n chance")
    public final int BLOOD_REGEN_CHANCE = 20;

    //    @DefaultDouble(value = 30D, minValue = 10D, maxValue = 10000D, name = "hunter_max_health")
    public final double VAMPIRE_HUNTER_MAX_HEALTH = 30;
    //    @DefaultDouble(value = 4D, minValue = 0D, maxValue = 10000, name = "hunter_max_health_pl", comment = "Max health is increased by this for every level the hunter has")
    public final double VAMPIRE_HUNTER_MAX_HEALTH_PL = 4;
    //    @DefaultDouble(value = 3D, minValue = 0D, name = "hunter_attack_damage")
    public final double VAMPIRE_HUNTER_ATTACK_DAMAGE = 3;
    //    @DefaultDouble(value = 1.5D, minValue = 0D, name = "hunter_attack_damage_pl")
    public final double VAMPIRE_HUNTER_ATTACK_DAMAGE_PL = 1.5;
    //    @DefaultDouble(value = 0.28D, minValue = 0.1, maxValue = 2, name = "hunter_speed")
    public final double VAMPIRE_HUNTER_SPEED = 0.28;

    //    @DefaultDouble(value = 20D, minValue = 10D, maxValue = 10000D, name = "hunter_villager_max_health")
    public final double HUNTER_VILLAGER_MAX_HEALTH = 20;
    //    @DefaultDouble(value = 2D, minValue = 0D, name = "hunter_villager_attack_damage")
    public final double HUNTER_VILLAGER_ATTACK_DAMAGE = 2;

    //    @DefaultDouble(value = 60D, minValue = 10D, maxValue = 10000D, name = "advanced_hunter_max_health")
    public final double ADVANCED_HUNTER_MAX_HEALTH = 60;
    //    @DefaultDouble(value = 30D, minValue = 0D, maxValue = 10000, name = "advanced_hunter_max_health_pl", comment = "Max health is increased by this for every level the hunter has")
    public final double ADVANCED_HUNTER_MAX_HEALTH_PL = 30;
    //    @DefaultDouble(value = 5D, minValue = 0D, name = "advanced_hunter_attack_damage")
    public final double ADVANCED_HUNTER_ATTACK_DAMAGE = 5;
    //    @DefaultDouble(value = 3D, minValue = 0D, name = "advanced_hunter_attack_damage_pl")
    public final double ADVANCED_HUNTER_ATTACK_DAMAGE_PL = 3;
    //    @DefaultDouble(value = 0.285D, minValue = 0.1, maxValue = 2, name = "advanced_hunter_speed")
    public final double ADVANCED_HUNTER_SPEED = 0.285;

    //    @DefaultDouble(value = 60D, minValue = 10D, maxValue = 10000D, name = "advanced_vampire_max_health")
    public final double ADVANCED_VAMPIRE_MAX_HEALTH = 60;
    //    @DefaultDouble(value = 40D, minValue = 0D, maxValue = 10000, name = "advanced_vampire_max_health_pl", comment = "Max health is increased by this for every level the hunter has")
    public final double ADVANCED_VAMPIRE_MAX_HEALTH_PL = 40;
    //    @DefaultDouble(value = 7D, minValue = 0D, name = "advanced_vampire_attack_damage")
    public final double ADVANCED_VAMPIRE_ATTACK_DAMAGE = 7;
    //    @DefaultDouble(value = 5D, minValue = 0D, name = "advanced_vampire_attack_damage_pl")
    public final double ADVANCED_VAMPIRE_ATTACK_DAMAGE_PL = 5;
    //    @DefaultDouble(value = 0.285D, minValue = 0.1, maxValue = 2, name = "advanced_vampire_speed")
    public final double ADVANCED_VAMPIRE_SPEED = 0.285;
    //    @DefaultInt(value = 5, minValue = 0, maxValue = 100, comment = "Maximum number of vampires that will follow an advanced vampire")
    public final int ADVANCED_VAMPIRE_MAX_FOLLOWER = 5;

    //    @DefaultDouble(value = 3, minValue = 0, comment = "Any fire damage that is received by an advanced vampire is multiplied by this")
    public final double ADVANCED_VAMPIRE_FIRE_VULNERABILITY = 3;


    //    @DefaultDouble(value = 30D, minValue = 10D, maxValue = 10000D, name = "vampire_max_health")
    public final double VAMPIRE_MAX_HEALTH = 30;
    //    @DefaultDouble(value = 3D, minValue = 0D, maxValue = 10000, name = "vampire_max_health_pl", comment = "Max health is increased by this for every level the vampire has")
    public final double VAMPIRE_MAX_HEALTH_PL = 3d;
    //    @DefaultDouble(value = 3D, minValue = 0D, name = "vampire_attack_damage")
    public final double VAMPIRE_ATTACK_DAMAGE = 3d;
    //    @DefaultDouble(value = 1D, minValue = 0D, name = "vampire_attack_damage_pl")
    public final double VAMPIRE_ATTACK_DAMAGE_PL = 1;
    //    @DefaultDouble(value = 0.3D, minValue = 0.1, maxValue = 2, name = "vampire_speed")
    public final double VAMPIRE_SPEED = 0.3;
    //    @DefaultDouble(value = 2, minValue = 0, comment = "Any fire damage that is received by a basic vampire is multiplied by this")
    public final double VAMPIRE_FIRE_VULNERABILITY = 2;

    //    @DefaultDouble(value = 7.0, minValue = 0.0, maxValue = 1000, name = "vampire_mob_sun_damage")
    public final double VAMPIRE_MOB_SUN_DAMAGE = 7;


    //    @DefaultDouble(value = 140.0D, minValue = 20.0D, maxValue = 300.0D, comment = "")
    public final double VAMPIRE_BARON_MAX_HEALTH = 140;
    //    @DefaultDouble(value = 6.0D, minValue = 1.0D, maxValue = 14.0D, comment = "")
    public final double VAMPIRE_BARON_ATTACK_DAMAGE = 6;
    //    @DefaultDouble(value = 0.34D, minValue = 0.1D, maxValue = 0.6D, comment = "")
    public final double VAMPIRE_BARON_MOVEMENT_SPEED = 0.34;
    //    @DefaultDouble(value = 1.20D, minValue = 1.0D, maxValue = 2.0D, comment = "For each higher level the stats are multiplied with this factor")
    public final double VAMPIRE_BARON_IMPROVEMENT_PER_LEVEL = 1.2;
    //    @DefaultDouble(value = 3, minValue = 0, comment = "Any fire damage that is received by a baron is multiplied by this")
    public final double VAMPIRE_BARON_FIRE_VULNERABILITY = 3;


    public final double MINION_MAX_HEALTH = 45;
    public final double MINION_MAX_HEALTH_PL = 5;
    public final double MINION_ATTACK_DAMAGE = 3;
    public final double MINION_ATTACK_DAMAGE_PL = 1;
}

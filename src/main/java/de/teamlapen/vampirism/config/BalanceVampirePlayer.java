package de.teamlapen.vampirism.config;

import de.teamlapen.lib.lib.config.BalanceValues;
import de.teamlapen.lib.lib.config.DefaultBoolean;
import de.teamlapen.lib.lib.config.DefaultDouble;
import de.teamlapen.lib.lib.config.DefaultInt;
import de.teamlapen.vampirism.VampirismMod;

import java.io.File;

/**
 * Balance values for vampire players.
 */
public class BalanceVampirePlayer extends BalanceValues {
    @DefaultDouble(value = 16D, minValue = 0.5D, maxValue = 2.0D, name = "health_max_modifier", comment = "Maximum amount of extra health")
    public double HEALTH_MAX_MOD;
    @DefaultInt(value = 14, minValue = 10, maxValue = 40, name = "health_level_cap", comment = "Level at which the maximum added health is reached")
    public int HEALTH_LCAP;
    @DefaultDouble(value = 0.5D, minValue = 0.5D, maxValue = 1.0D, name = "health_modifier_type", comment = "0.5 for square root, 1 for linear")
    public double HEALTH_TYPE;
    @DefaultDouble(value = 1.0D, minValue = 0.5D, maxValue = 2.0D, name = "strength_max_modifier", comment = "")
    public double STRENGTH_MAX_MOD;
    @DefaultInt(value = 20, minValue = 10, maxValue = 40, name = "strength_modifier_level_cap", comment = "")
    public int STRENGTH_LCAP;
    @DefaultDouble(value = 0.5D, minValue = 0.5D, maxValue = 1.0D, name = "strength_modifier_type", comment = "0.5 for square root, 1 for linear")
    public double STRENGTH_TYPE;
    @DefaultDouble(value = 0.3D, minValue = 0.0D, maxValue = 5D, name = "speed_max_modifier", comment = "")
    public double SPEED_MAX_MOD;
    @DefaultInt(value = 15, minValue = 7, maxValue = 100, name = "speed_modifier_level_cap", comment = "")
    public int SPEED_LCAP;
    @DefaultDouble(value = 0.5D, minValue = 0.1D, maxValue = 1.0D, name = "speed_modifier_type", comment = "")
    public double SPEED_TYPE;
    @DefaultDouble(value = 0.5D, minValue = 0.1D, maxValue = 1.0D, name = "speed_modifier_type", comment = "")
    public double EXHAUSTION_TYPE;
    @DefaultDouble(value = 1D, minValue = 0D, maxValue = 10, name = "exhaustion_modifier_max")
    public double EXAUSTION_MAX_MOD;
    //    @DefaultDouble(value = 0.2D, minValue = 0.1D, maxValue = 0.4D, name = "Jump Max Boost", comment = "")
//    public double JUMP_MAX_BOOST;
//    @DefaultInt(value = 6, minValue = 3, maxValue = 100, name = "Jump Level Cap", comment = "")
//    public int JUMP_LCAP;
//    @DefaultDouble(value = 0.5D, minValue = 0.1D, maxValue = 1.0D, name = "Jump Type", comment = "")
//    public double JUMP_TYPE;
    @DefaultDouble(value = 0.7D, name = "blood_exhaustion_basic_modifier", minValue = 0, maxValue = 5, comment = "Blood exhaustion is multiplied with this value")
    public double BLOOD_EXHAUSTION_BASIC_MOD;
    //    @DefaultBoolean(value = true, name = "blood_increase_exhaustion", comment = "Increase exhaustion modifier with higher levels")
//    public boolean BLOOD_INCREASE_EXHAUSTION;
    @DefaultBoolean(value = false, comment = "If the player should consume blood while in peaceful mode")
    public boolean BLOOD_USAGE_PEACEFUL;
    @DefaultInt(value = 6, name = "bite_damage", minValue = 0)
    public int BITE_DMG;
    @DefaultInt(value = 15, name = "bite_cooldown", minValue = 1, comment = "Cooldown for vampire player bites in ticks")
    public int BITE_COOLDOWN;
    @DefaultDouble(value = 1.5F, name = "player_blood_saturation", minValue = 0.3D)
    public double PLAYER_BLOOD_SATURATION;
    @DefaultInt(value = 900, name = "sanguinare_avg_duration", minValue = 1, comment = "Average duration of player sanguinare effect, in seconds")
    public int SANGUINARE_AVG_DURATION;
    @DefaultInt(value = 4, alternateValue = 2, name = "sundamage_min_level", minValue = 1, comment = "The vampire level as of the player receives damage from the sun")
    public int SUNDAMAGE_MINLEVEL;
    @DefaultBoolean(value = true, name = "sundamage_nausea", comment = "If the player should get a nausea effect if in sun")
    public boolean SUNDAMAGE_NAUSEA;
    @DefaultInt(value = 3, alternateValue = 1, minValue = 1, comment = "The vampire level as of the player receives a nausea effect from the sun")
    public int SUNDAMAGE_NAUSEA_MINLEVEL;
    @DefaultInt(value = 2, alternateValue = 1, minValue = 1, comment = "The vampire level as of the player receives a weakness effect from the sun")
    public int SUNDAMAGE_WEAKNESS_MINLEVEL;
    @DefaultDouble(value = 7.0, alternateValue = 14, name = "sundamage_damage", minValue = 1, comment = "Damage a player receives every 2 seconds if in sun. Is multiplied with several factors.")
    public double SUNDAMAGE_DAMAGE;

    @DefaultInt(value = 4, alternateValue = 8, name = "sundamage_water_blocks", minValue = 1, comment = "How many blocks deep into the water has a vampire to be to not receive sundamage. Due to performance optimisation only used when player below sea level")
    public int SUNDAMAGE_WATER_BLOCKS;

    @DefaultDouble(value = 2.0, name = "garlic_damage", minValue = 1, comment = "UNUSED - Damage the player receives every 2 seconds if in garlic")
    public double GARLIC_DAMAGE;

    @DefaultInt(value = 14, minValue = 1, comment = "Level as of the fire vulnerability will not be increased")
    public int FIRE_VULNERABILITY_LCAP;
    @DefaultDouble(value = 0.5, minValue = 0, comment = "Type of value calculation, 0.5 for square root, 1 for linear")
    public double FIRE_VULNERABILITY_TYPE;
    @DefaultDouble(value = 4, minValue = 0.1, comment = "Max modifier for fire damage. Old value is multiplied with this.")
    public double FIRE_VULNERABILITY_MAX_MOD;

    /**
     * Creates a configuration for balance values
     *
     * @param directory
     */
    public BalanceVampirePlayer(File directory) {
        super("vampire_player", directory);
    }

    @Override
    protected boolean shouldUseAlternate() {
        return VampirismMod.isRealism();
    }
}

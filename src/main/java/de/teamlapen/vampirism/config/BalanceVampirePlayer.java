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
    @DefaultDouble(value = 1.0D, minValue = 0.5D, maxValue = 2.0D, name = "health_max_odifier", comment = "")
    public double HEALTH_MAX_MOD;
    @DefaultInt(value = 20, minValue = 10, maxValue = 40, name = "health_level_cap", comment = "")
    public int HEALTH_LCAP;
    @DefaultDouble(value = 0.5D, minValue = 0.5D, maxValue = 1.0D, name = "health_modifier_type", comment = "0.5 for square root, 1 for linear")
    public double HEALTH_TYPE;
    @DefaultDouble(value = 1.0D, minValue = 0.5D, maxValue = 2.0D, name = "strength_max_modifier", comment = "")
    public double STRENGTH_MAX_MOD;
    @DefaultInt(value = 20, minValue = 10, maxValue = 40, name = "strength_level_cap", comment = "")
    public int STRENGTH_LCAP;
    @DefaultDouble(value = 0.5D, minValue = 0.5D, maxValue = 1.0D, name = "strength_modifier_type", comment = "0.5 for square root, 1 for linear")
    public double STRENGTH_TYPE;
    @DefaultDouble(value = 0.3D, minValue = 0.15D, maxValue = 5D, name = "speed_max_modifier", comment = "")
    public double SPEED_MAX_MOD;
    @DefaultInt(value = 15, minValue = 7, maxValue = 100, name = "speed_level_cap", comment = "")
    public int SPEED_LCAP;
    @DefaultDouble(value = 0.5D, minValue = 0.1D, maxValue = 1.0D, name = "speed_modifier_type", comment = "")
    public double SPEED_TYPE;
    //    @DefaultDouble(value = 0.2D, minValue = 0.1D, maxValue = 0.4D, name = "Jump Max Boost", comment = "")
//    public double JUMP_MAX_BOOST;
//    @DefaultInt(value = 6, minValue = 3, maxValue = 100, name = "Jump Level Cap", comment = "")
//    public int JUMP_LCAP;
//    @DefaultDouble(value = 0.5D, minValue = 0.1D, maxValue = 1.0D, name = "Jump Type", comment = "")
//    public double JUMP_TYPE;
    @DefaultDouble(value = 1.0D, name = "blood_exhaustion_modifier", minValue = 0, maxValue = 5, comment = "Blood exhaustion is multiplied with this value")
    public double BLOOD_EXHAUSTION_MOD;
    @DefaultBoolean(value = true, name = "blood_increase_exhaustion", comment = "Increase exhaustion modifier with higher levels")
    public boolean BLOOD_INCREASE_EXHAUSTION;
    @DefaultInt(value = 6, name = "bite_damage", minValue = 0)
    public int BITE_DMG;
    @DefaultInt(value = 15, name = "bite_cooldown", minValue = 1, comment = "Cooldown for vampire player bites in ticks")
    public int BITE_COOLDOWN;
    @DefaultDouble(value = 1.5F, name = "player_blood_saturation", minValue = 0.3D)
    public double PLAYER_BLOOD_SATURATION;
    @DefaultInt(value = 900, name = "sanguinare_avg_duration", minValue = 1, comment = "Average duration of player sanguinare effect, in seconds")
    public int SANGUINARE_AVG_DURATION;

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

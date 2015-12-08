package de.teamlapen.vampirism.config;

import de.teamlapen.lib.config.BalanceValues;
import de.teamlapen.lib.config.DefaultDouble;
import de.teamlapen.lib.config.DefaultInt;
import de.teamlapen.vampirism.VampirismMod;

import java.io.File;

/**
 * Balance values for vampire players.
 */
public class BalanceVampirePlayer extends BalanceValues {
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

    @DefaultDouble(value = 1.0D, minValue = 0.5D, maxValue = 2.0D, name = "Health Max Modifier", comment = "")
    public static double HEALTH_MAX_MOD;
    @DefaultInt(value = 20, minValue = 10, maxValue = 40, name = "Health Level Cap", comment = "")
    public static int HEALTH_LCAP;
    @DefaultDouble(value = 0.5D, minValue = 0.5D, maxValue = 1.0D, name = "Health Type", comment = "0.5 for square root, 1 for linear")
    public static double HEALTH_TYPE;
    @DefaultDouble(value = 1.0D, minValue = 0.5D, maxValue = 2.0D, name = "Strength Max Modifier", comment = "")
    public static double STRENGTH_MAX_MOD;
    @DefaultInt(value = 20, minValue = 10, maxValue = 40, name = "Strength Level Cap", comment = "")
    public static int STRENGTH_LCAP;
    @DefaultDouble(value = 0.5D, minValue = 0.5D, maxValue = 1.0D, name = "Strength Modifier Type", comment = "0.5 for square root, 1 for linear")
    public static double STRENGTH_TYPE;
    @DefaultDouble(value = 0.3D, minValue = 0.15D, maxValue = 5D, name = "Speed Max Modifier", comment = "")
    public static double SPEED_MAX_MOD;
    @DefaultInt(value = 15, minValue = 7, maxValue = 100, name = "Speed Level Cap", comment = "")
    public static int SPEED_LCAP;
    @DefaultDouble(value = 0.5D, minValue = 0.1D, maxValue = 1.0D, name = "Speed Type", comment = "")
    public static double SPEED_TYPE;
    @DefaultDouble(value = 0.2D, minValue = 0.1D, maxValue = 0.4D, name = "Jump Max Boost", comment = "")
    public static double JUMP_MAX_BOOST;
    @DefaultInt(value = 6, minValue = 3, maxValue = 100, name = "Jump Level Cap", comment = "")
    public static int JUMP_LCAP;
    @DefaultDouble(value = 0.5D, minValue = 0.1D, maxValue = 1.0D, name = "Jump Type", comment = "")
    public static double JUMP_TYPE;

    @DefaultInt(value = 3,name="Turn Others Level",minValue = 1,hasAlternate = true,alternateValue = 1,comment = "Level as of which a vampire player is able to infect other players")
    public static int MIN_TURN_LEVEL;
}

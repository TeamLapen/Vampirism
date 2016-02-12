package de.teamlapen.vampirism.config;

import de.teamlapen.lib.lib.config.BalanceValues;
import de.teamlapen.lib.lib.config.DefaultDouble;
import de.teamlapen.lib.lib.config.DefaultInt;
import de.teamlapen.vampirism.VampirismMod;

import java.io.File;

public class BalanceHunterPlayer extends BalanceValues {
    @DefaultDouble(value = 1.0D, minValue = 0.5D, maxValue = 2.0D, name = "Strength Max Modifier", comment = "")
    public double STRENGTH_MAX_MOD;
    @DefaultInt(value = 20, minValue = 10, maxValue = 40, name = "Strength Level Cap", comment = "")
    public int STRENGTH_LCAP;
    @DefaultDouble(value = 0.5D, minValue = 0.5D, maxValue = 1.0D, name = "Strength Modifier Type", comment = "0.5 for square root, 1 for linear")
    public double STRENGTH_TYPE;
    /**
     * Creates a configuration for balance values
     *
     * @param directory
     */
    public BalanceHunterPlayer( File directory) {
        super("hunter_player", directory);
    }

    @Override
    protected boolean shouldUseAlternate() {
        return VampirismMod.isRealism();
    }
}

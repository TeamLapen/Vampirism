package de.teamlapen.vampirism.config;

import de.teamlapen.lib.lib.config.BalanceValues;
import de.teamlapen.lib.lib.config.DefaultInt;
import de.teamlapen.vampirism.VampirismMod;

import java.io.File;

/**
 * For all balance values that don't belong anywhere else
 */
public class BalanceGeneral extends BalanceValues {

    @DefaultInt(value = 6, name = "garlic_check_range", minValue = 1, maxValue = 20, comment = "The distance in which a garlic block has to be to harm. High values might have a performance impact")
    public static int GARLIC_CHECK_RANGE;
    @DefaultInt(value = 3, name = "garlic_check_range_vertical", minValue = 1, maxValue = 20, comment = "The vertical distance in which a garlic block has to be to harm. High values might have a performance impact")
    public static int GARLIC_CHECK_VERTICAL_RANGE;
    @DefaultInt(value = 10, name = "vampire_forest_weight", minValue = 1)
    public static int VAMPIRE_FOREST_WEIGHT;

    /**
     * Creates a configuration for balance values
     *
     * @param directory
     */
    public BalanceGeneral(File directory) {
        super("general", directory);
    }

    @Override
    protected boolean shouldUseAlternate() {
        return VampirismMod.isRealism();
    }
}

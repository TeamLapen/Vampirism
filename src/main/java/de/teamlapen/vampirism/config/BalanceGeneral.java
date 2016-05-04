package de.teamlapen.vampirism.config;

import de.teamlapen.lib.lib.config.BalanceValues;
import de.teamlapen.lib.lib.config.DefaultBoolean;
import de.teamlapen.lib.lib.config.DefaultInt;
import de.teamlapen.vampirism.VampirismMod;

import java.io.File;

/**
 * For all balance values that don't belong anywhere else
 */
public class BalanceGeneral extends BalanceValues {

    @DefaultInt(value = 6, name = "garlic_check_range", minValue = 1, maxValue = 20, comment = "The distance in which a garlic block has to be to harm. High values might have a performance impact")
    public int GARLIC_CHECK_RANGE;
    @DefaultInt(value = 3, name = "garlic_check_range_vertical", minValue = 1, maxValue = 20, comment = "The vertical distance in which a garlic block has to be to harm. High values might have a performance impact")
    public int GARLIC_CHECK_VERTICAL_RANGE;
    @DefaultInt(value = 10, name = "vampire_forest_weight", minValue = 1)
    public int VAMPIRE_FOREST_WEIGHT;

    @DefaultInt(value = 40, minValue = 0, maxValue = 1000, name = "hunter_camp_spawn_chance", comment = "Chance that a camp is generated. n/1000 for each valid chunk")
    public int HUNTER_CAMP_SPAWN_CHANCE;

    @DefaultBoolean(value = true, alternateValue = false, hasAlternate = true, comment = "If the sanguinare effect can be canceled by a milk bucket")
    public boolean CAN_CANCEL_SANGUINARE;

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

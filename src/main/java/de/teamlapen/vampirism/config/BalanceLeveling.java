package de.teamlapen.vampirism.config;

import de.teamlapen.lib.lib.config.BalanceValues;
import de.teamlapen.lib.lib.config.DefaultInt;
import de.teamlapen.vampirism.VampirismMod;

import java.io.File;

/**
 * Stores balance values for leveling
 */
public class BalanceLeveling extends BalanceValues {

    @DefaultInt(value = 2, maxValue = 20, minValue = 0, comment = "Testing purpose", name = "test_value", alternateValue = 6, hasAlternate = true)
    public int TEST_VALUE;

    public BalanceLeveling(File directory) {
        super("leveling", directory);
    }

    @Override
    protected boolean shouldUseAlternate() {
        return VampirismMod.isRealism();
    }
}

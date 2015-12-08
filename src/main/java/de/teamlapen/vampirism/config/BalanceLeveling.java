package de.teamlapen.vampirism.config;

import de.teamlapen.lib.config.BalanceValues;
import de.teamlapen.lib.config.DefaultInt;
import de.teamlapen.vampirism.VampirismMod;

import java.io.File;

/**
 * Stores balance values for leveling
 */
public class BalanceLeveling extends BalanceValues {

    public BalanceLeveling(File directory) {
        super("leveling", directory);
    }

    @DefaultInt(value = 2,maxValue = 20,minValue = 0,comment = "Testing purpose",name = "test_value",alternateValue = 6,hasAlternate = true)
    public int TEST_VALUE;

    @Override
    protected boolean shouldUseAlternate() {
        return VampirismMod.isRealism();
    }
}

package de.teamlapen.vampirism.config;

import de.teamlapen.lib.lib.config.BalanceValues;
import de.teamlapen.lib.lib.config.DefaultBoolean;
import de.teamlapen.vampirism.VampirismMod;

import java.io.File;

/**
 * Balance values for villages
 */
public class BalanceVillage extends BalanceValues {

    @DefaultBoolean(value = true, comment = "If grass should slowly be replaced by cursed earths if vampire village")
    public boolean REPLACE_BLOCKS;
    /**
     * Creates a configuration for balance values
     *
     * @param directory
     */
    public BalanceVillage(File directory) {
        super("village", directory);
    }

    @Override
    protected boolean shouldUseAlternate() {
        return VampirismMod.isRealism();
    }
}

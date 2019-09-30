package de.teamlapen.vampirism.config;

import de.teamlapen.lib.lib.config.BalanceValues;
import de.teamlapen.lib.lib.config.DefaultBoolean;
import de.teamlapen.lib.lib.config.DefaultInt;
import de.teamlapen.vampirism.VampirismMod;

import java.io.File;

/**
 * Balance values for villages
 */
public class BalanceVillage extends BalanceValues {

    @DefaultBoolean(value = true, comment = "If grass should slowly be replaced by cursed earths if vampire village")
    public boolean REPLACE_BLOCKS;
    @DefaultInt(value = 80, comment = "Duration of phase 1 of the capturing process")
    public int DURATION_PHASE_1;
    @DefaultInt(value = 40000, comment = "Distance of village notification about capturing")
    public int NOTIFY_DISTANCE_SQ;
    @DefaultInt(value = 80, comment = "Time in capture phase 2 after which the capture entities should find a target regardless of distance")
    public int FORCE_TAGET_TIME;
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

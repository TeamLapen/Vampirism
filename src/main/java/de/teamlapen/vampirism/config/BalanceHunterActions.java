package de.teamlapen.vampirism.config;

import de.teamlapen.lib.lib.config.BalanceValues;
import de.teamlapen.lib.lib.config.DefaultBoolean;
import de.teamlapen.lib.lib.config.DefaultDouble;
import de.teamlapen.lib.lib.config.DefaultInt;
import de.teamlapen.vampirism.VampirismMod;

import java.io.File;

/**
 * Balance values for Hunter Player Actions
 */
public class BalanceHunterActions extends BalanceValues {

    @DefaultDouble(value = 0.3D, minValue = 0, comment = "If disguised the detection radius of mobs will be multiplied by this")
    public double DISGUISE_VISIBILITY_MOD;
    @DefaultBoolean(value = true)
    public boolean DISGUISE_ENABLED;

    @DefaultInt(value = 120)
    public int DISGUISE_DURATION;//TODO
    @DefaultInt(value = 5)
    public int DISGUISE_COOLDOWN;//TODO change

    @DefaultInt(value = 1024, minValue = 1, comment = "Squared distance as of which a disguised hunter is invisible")
    public int DISGUISE_DISTANCE_INVISIBLE_SQ;

    /**
     * Creates a configuration for balance values
     *
     * @param directory
     */
    public BalanceHunterActions(File directory) {
        super("hunter_player_actions", directory);
    }

    @Override
    protected boolean shouldUseAlternate() {
        return VampirismMod.isRealism();
    }
}

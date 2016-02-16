package de.teamlapen.vampirism.config;

import de.teamlapen.lib.lib.config.BalanceValues;
import de.teamlapen.lib.lib.config.DefaultInt;
import de.teamlapen.vampirism.VampirismMod;

import java.io.File;

/**
 * Balance values for vampire players
 */
public class BalanceVampireSkills extends BalanceValues {

    @DefaultInt(value = 60, minValue = 1, name = "Freeze cooldown")
    public int FREEZE_COOLDOWN;

    @DefaultInt(value = 0, minValue = -1, maxValue = 0, name = "Freeze min level", comment = "Can only be -1 (disabled) or 0 (enabled). Only available for lords anyway.")
    public int FREEZE_MIN_LEVEL;

    @DefaultInt(value = 6, minValue = 1, maxValue = 30, name = "Freeze duration")
    public int FREEZE_DURATION;

    /**
     * Creates a configuration for balance values
     *
     * @param directory
     */
    public BalanceVampireSkills(File directory) {
        super("vampire_player_skills", directory);
    }

    @Override
    protected boolean shouldUseAlternate() {
        return VampirismMod.isRealism();
    }
}

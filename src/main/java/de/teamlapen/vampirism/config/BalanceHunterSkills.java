package de.teamlapen.vampirism.config;

import de.teamlapen.lib.lib.config.BalanceValues;
import de.teamlapen.lib.lib.config.DefaultDouble;
import de.teamlapen.vampirism.VampirismMod;

import java.io.File;

/**
 * Balance values for all hunter skills
 */
public class BalanceHunterSkills extends BalanceValues {


    @DefaultDouble(value = 0.2, comment = "Basic skill - Weapon cooldown = 1/(oldValue*(1+modifier))")
    public double SMALL_ATTACK_SPEED_MODIFIER;

    @DefaultDouble(value = 0.4, comment = "Advanced skill - Weapon cooldown = 1/(oldValue*(1+modifier))")
    public double MAJOR_ATTACK_SPPED_MODIFIER;

    /**
     * Creates a configuration for balance values
     *
     * @param directory
     */
    public BalanceHunterSkills(File directory) {
        super("hunter_skills", directory);
    }

    @Override
    protected boolean shouldUseAlternate() {
        return VampirismMod.isRealism();
    }
}

package de.teamlapen.vampirism.config;

import de.teamlapen.lib.lib.config.BalanceValues;
import de.teamlapen.lib.lib.config.DefaultBoolean;
import de.teamlapen.lib.lib.config.DefaultDouble;
import de.teamlapen.lib.lib.config.DefaultInt;
import de.teamlapen.vampirism.VampirismMod;

import java.io.File;

/**
 * Balance values for all hunter skills
 */
public class BalanceHunterSkills extends BalanceValues {


    @DefaultDouble(value = 0.2, comment = "Basic skill - Weapon cooldown = 1/(oldValue*(1+modifier))")
    public double SMALL_ATTACK_SPEED_MODIFIER;

    @DefaultDouble(value = 0.4, comment = "Advanced skill - Weapon cooldown = 1/(oldValue*(1+modifier))")
    public double MAJOR_ATTACK_SPEED_MODIFIER;

    @DefaultBoolean(value = false, comment = "First stake skill - If it is required to attack from behind to instant kill low level vampires")
    public boolean INSTANT_KILL_SKILL_1_FROM_BEHIND;

    @DefaultDouble(value = 0.3, minValue = 0, maxValue = 1, comment = "The maximal relative health (actual/max) a entity may have to be instantly killed")
    public double INSTANT_KILL_SKILL_1_MAX_HEALTH_PERC;

    @DefaultInt(value = 170, minValue = 0, comment = "Second stake skill - The max (not the actual) health a entity that can be one hit killed from behind may have")
    public int INSTANT_KILL_SKILL_2_MAX_HEALTH;

    @DefaultBoolean(value = false, comment = "Second stake skill - If only NPCs can be one hit killed with this skill")
    public boolean INSTANT_KILL_SKILL_2_ONLY_NPC;



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

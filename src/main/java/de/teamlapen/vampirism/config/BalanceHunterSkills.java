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

    @DefaultDouble(value = 0.35, minValue = 0, maxValue = 1, comment = "The maximal relative health (actual/max) a entity may have to be instantly killed")
    public double INSTANT_KILL_SKILL_1_MAX_HEALTH_PERC;

    @DefaultInt(value = 200, minValue = 0, comment = "Second stake skill - The max (not the actual) health a entity that can be one hit killed from behind may have")
    public int INSTANT_KILL_SKILL_2_MAX_HEALTH;

    @DefaultBoolean(value = false, comment = "Second stake skill - If only NPCs can be one hit killed with this skill")
    public boolean INSTANT_KILL_SKILL_2_ONLY_NPC;


    @DefaultInt(value = 0, comment = "The chunk radius a normal diffusor affects. 0 results in a one chunk area. Changing this only affects newly placed blocks", minValue = 0, maxValue = 5)
    public int GARLIC_DIFFUSOR_NORMAL_DISTANCE;

    @DefaultInt(value = 1, comment = "The chunk radius a enhanced diffusor affects. 1 results in a 3 by 3 chunk area", minValue = 0, maxValue = 5)
    public int GARLIC_DIFFUSOR_ENHANCED_DISTANCE;

    @DefaultInt(value = 2, comment = "The chunk radius a weak diffusor (only creative, only prevents spawns) affects. 2 results in a 5 by 5 chunk area", minValue = 0, maxValue = 5)
    public int GARLIC_DIFFUSOR_WEAK_DISTANCE;

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

package de.teamlapen.vampirism.config;

import de.teamlapen.lib.lib.config.BalanceValues;
import de.teamlapen.lib.lib.config.DefaultBoolean;
import de.teamlapen.lib.lib.config.DefaultDouble;
import de.teamlapen.lib.lib.config.DefaultInt;
import de.teamlapen.vampirism.VampirismMod;

import java.io.File;

/**
 * For all balance values that don't belong anywhere else
 */
public class BalanceGeneral extends BalanceValues {

    @DefaultInt(value = 10, name = "vampire_forest_weight", minValue = 1)
    public int VAMPIRE_FOREST_WEIGHT;


    @DefaultInt(value = 6, minValue = 3, maxValue = 10000, name = "hunter_camp_density", comment = "Minecraft will try to generate 1 camp per NxN chunk area.")
    public int HUNTER_CAMP_DENSITY;

    @DefaultBoolean(value = true, alternateValue = false, hasAlternate = true, comment = "If the sanguinare effect can be canceled by a milk bucket")
    public boolean CAN_CANCEL_SANGUINARE;

    @DefaultInt(value = 40, alternateValue = 50, hasAlternate = true, comment = "The vampire killer arrow can only instant kill NPC vampires that have a max (not actual) health of this")
    public int ARROW_VAMPIRE_KILLER_MAX_HEALTH;

    @DefaultInt(value = 5, minValue = 0, comment = "Damage a normal holy water splash bottle does when directly hitting a vampire")
    public int HOLY_WATER_SPLASH_DAMAGE;
    @DefaultDouble(value = 1.5, minValue = 1, comment = "Holy water damage is multiplied with this value for each tier above normal")
    public double HOLY_WATER_TIER_DAMAGE_INC;

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

package de.teamlapen.vampirism.config;

import de.teamlapen.lib.lib.config.BalanceValues;
import de.teamlapen.lib.lib.config.DefaultBoolean;
import de.teamlapen.lib.lib.config.DefaultDouble;
import de.teamlapen.lib.lib.config.DefaultInt;
import de.teamlapen.vampirism.VampirismMod;

import java.io.File;

/**
 * Balance values for villages and {@link VampirismVillage}
 */
public class BalanceVillage extends BalanceValues {

    @DefaultInt(value = 60, minValue = 1, name = "villager_forgiveness_rate", comment = "Determines how fast the villagers forget about their bitten citizens. In seconds.")
    public int REDUCE_RATE;

    @DefaultInt(value = 2, minValue = 1, comment = "The chance that a new villager is spawned for a converted one. 1/n")
    public int VILLAGER_RESPAWN_RATE;

    @DefaultInt(value = 20, minValue = 0, comment = "How much biting a villager counts towards the aggressive counter")
    public int BITTEN_AGGRESSIVE_FACTOR;

    @DefaultInt(value = 30, minValue = 0, comment = "How much killing a villager with a vampire bite counts towards the aggressive counter")
    public int BITTEN_TO_DEATH_AGGRESSIVE_FACTOR;

    @DefaultInt(value = 40, minValue = 0, comment = "How much converting a villager with a vampire bite counts towards the aggressive counter")
    public int CONVERTED_AGGRESSIVE_FACTOR;

    @DefaultInt(value = 190, minValue = 1, comment = "As of which aggressive counter value, villagers become aggressive")
    public int AGGRESSIVE_COUNTER_THRESHOLD;

    @DefaultInt(value = 4, minValue = 1, comment = "If a village becomes aggressive this is the chance for a valid villager to turn into a hunter (1/n)")
    public int VILLAGER_HUNTER_CHANCE;

    @DefaultDouble(value = 0.25, minValue = 0, comment = "The number of hunters that should be in a village (approximately) per door. (There are around 0.35 villager per door)")
    public double MIN_HUNTER_COUNT_VILLAGE_PER_DOOR;

    @DefaultBoolean(value = true, comment = "Spawn vampires instead of hunters in overtaken villages")
    public boolean SPAWN_VAMPIRE_IN_OVERTAKEN;

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

package de.teamlapen.vampirism.config;

import de.teamlapen.lib.lib.config.BalanceValues;
import de.teamlapen.lib.lib.config.DefaultInt;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.world.villages.VampirismVillage;

import java.io.File;

/**
 * Balance values for villages and {@link VampirismVillage}
 */
public class BalanceVillage extends BalanceValues {

    @DefaultInt(value = 50, minValue = 1, name = "villager_forgiveness_rate", comment = "Determines how fast the villagers forget about their bitten citizens. In seconds.")
    public int REDUCE_RATE;

    @DefaultInt(value = 2, minValue = 1, comment = "The chance that a new villager is spawned for a converted one. 1/n")
    public int VILLAGER_RESPAWN_RATE;

    @DefaultInt(value = 4, minValue = 1, comment = "How many villagers can be bitten until the village get aggressive")
    public int BITTEN_UNTIL_AGRESSIVE;
    @DefaultInt(value = 4, minValue = 1, comment = "How many villagers have to be converted or killed by vampires until the village get aggressive")
    public int CONVERTED_UNTIL_AGRESSIVE;

    @DefaultInt(value = 2, comment = "The number of hunters that should be in a village (approximately)")
    public int MIN_HUNTER_COUNT_VILLAGE;

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

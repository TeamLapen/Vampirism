package de.teamlapen.vampirism.config;

import de.teamlapen.lib.lib.config.BalanceValues;
import de.teamlapen.lib.lib.config.DefaultInt;
import de.teamlapen.vampirism.VampirismMod;
import java.io.File;

public class BalanceEntityActions extends BalanceValues {

    @DefaultInt(value = 5, minValue = 1, name = "Vampire Invisible Cooldown", comment = "In seconds")
    public int VAMPIRE_INVISIBLE_COOLDOWN;

    @DefaultInt(value = 30, minValue = 1, name = "Vampire Invisible Duration", comment = "In Minecraft Ticks, 20 Ticks = 1 sec")
    public int VAMPIRE_INVISIBLE_DURATION;

    @DefaultInt(value = 5, minValue = 1, name = "Vampire Invisible Cooldown", comment = "In seconds")
    public int VAMPIRE_HEAL_COOLDOWN;

    @DefaultInt(value = 20, minValue = 0, maxValue = 100, name = "Vampire Heal Amount", comment = "In Percent")
    public int VAMPIRE_HEAL_AMOUNT;


    /**
     * Creates a configuration for balance values
     *
     * @param directory
     */
    public BalanceEntityActions(File directory) {
        super("vampire_entity_actions", directory);
    }

    @Override
    protected boolean shouldUseAlternate() {
        return VampirismMod.isRealism();
    }

}

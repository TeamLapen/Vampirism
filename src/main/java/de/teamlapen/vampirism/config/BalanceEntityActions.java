package de.teamlapen.vampirism.config;

import de.teamlapen.lib.lib.config.BalanceValues;
import de.teamlapen.lib.lib.config.DefaultInt;
import de.teamlapen.vampirism.VampirismMod;
import java.io.File;

public class BalanceEntityActions extends BalanceValues {

    /* Invisible Action */
    @DefaultInt(value = 5, minValue = 1, name = "Invisible Cooldown", comment = "In seconds")
    public int INVISIBLE_COOLDOWN;

    @DefaultInt(value = 30, minValue = 1, name = "Invisible Duration", comment = "In Minecraft Ticks, 20 Ticks = 1 sec")
    public int INVISIBLE_DURATION;

    /* Heal Action */
    @DefaultInt(value = 5, minValue = 1, name = "Invisible Cooldown", comment = "In seconds")
    public int HEAL_COOLDOWN;

    @DefaultInt(value = 20, minValue = 0, maxValue = 100, name = "Heal Amount", comment = "In Percent")
    public int HEAL_AMOUNT;

    /* Regeneration Action */
    @DefaultInt(value = 10, minValue = 0, name = "Regeneration Duration", comment = "In seconds")
    public int REGENERATION_DURATION;

    @DefaultInt(value = 5, minValue = 1, name = "Regeneration Cooldown", comment = "In seconds")
    public int REGENERATION_COOLDOWN;

    @DefaultInt(value = 20, minValue = 0, maxValue = 100, name = "Regeneration Amount", comment = "In Percent")
    public int REGENERATION_AMOUNT;

    /* Speed Action */
    @DefaultInt(value = 10, minValue = 0, name = "Speed Duration", comment = "In seconds")
    public int SPEED_DURATION;

    @DefaultInt(value = 5, minValue = 1, name = "Speed Cooldown", comment = "In seconds")
    public int SPEED_COOLDOWN;

    @DefaultInt(value = 20, minValue = 0, maxValue = 100, name = "Speed Amount", comment = "In Percent")
    public int SPEED_AMOUNT;


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

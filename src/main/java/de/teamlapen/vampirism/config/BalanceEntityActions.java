package de.teamlapen.vampirism.config;

import de.teamlapen.lib.lib.config.BalanceValues;
import de.teamlapen.lib.lib.config.DefaultDouble;
import de.teamlapen.lib.lib.config.DefaultInt;
import de.teamlapen.vampirism.VampirismMod;
import java.io.File;

public class BalanceEntityActions extends BalanceValues {

    /* Invisible Action */
    @DefaultInt(value = 5, minValue = 1, name = "Invisible Cooldown", comment = "In seconds")
    public int INVISIBLE_COOLDOWN;

    @DefaultInt(value = 30, minValue = 1, name = "Invisible Duration", comment = "In ticks")
    public int INVISIBLE_DURATION;

    /* Heal Action */
    @DefaultInt(value = 6, minValue = 1, name = "Invisible Cooldown", comment = "In seconds")
    public int HEAL_COOLDOWN;

    @DefaultInt(value = 20, minValue = 0, maxValue = 100, name = "Heal Amount", comment = "In percent")
    public int HEAL_AMOUNT;

    /* Regeneration Action */
    @DefaultInt(value = 5, minValue = 0, name = "Regeneration Duration", comment = "In seconds")
    public int REGENERATION_DURATION;

    @DefaultInt(value = 4, minValue = 1, name = "Regeneration Cooldown", comment = "In seconds")
    public int REGENERATION_COOLDOWN;

    @DefaultInt(value = 30, minValue = 0, maxValue = 100, name = "Regeneration Amount", comment = "In percent")
    public int REGENERATION_AMOUNT;

    /* Speed Action */
    @DefaultInt(value = 4, minValue = 0, name = "Speed Duration", comment = "In seconds")
    public int SPEED_DURATION;

    @DefaultInt(value = 5, minValue = 1, name = "Speed Cooldown", comment = "In seconds")
    public int SPEED_COOLDOWN;

    @DefaultDouble(value = 0.1, name = "Speed Amount", comment = "Speed = basevalue * (1 + SPEED_AMOUNT)")
    public double SPEED_AMOUNT;

    /* Bat Spawn Action */
    @DefaultInt(value = 5, minValue = 1, name = "Batspawn Cooldown")
    public int BATSPAWN_COOLDOWN;

    @DefaultInt(value = 5, minValue = 1, name = "Batspawn Amount")
    public int BATSPAWN_AMOUNT;

    /* Dark Projectile Action */
    @DefaultInt(value = 6, minValue = 1, comment = "In seconds")
    public int DARK_PROJECTILE_COOLDOWN;

    @DefaultDouble(value = 4, minValue = 0, comment = "Damage of the direct projectile hit")
    public double DARK_BLOOD_PROJECTILE_DAMAGE;

    @DefaultDouble(value = 1, minValue = 0, comment = "Damage of the indirect projectile hit")
    public double DARK_BLOOD_PROJECTILE_INDIRECT_DAMAGE;

    /* Sunscreen Action */
    @DefaultInt(value = 5, minValue = 1, name = "Sunscreen Duration", comment = "In seconds")
    public int SUNSCREEN_DURATION;

    @DefaultInt(value = 3, minValue = 1, name = "Sunscreen Cooldown", comment = "In seconds")
    public int SUNSCREEN_COOLDOWN;


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

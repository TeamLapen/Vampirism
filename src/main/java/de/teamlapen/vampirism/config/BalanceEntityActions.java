package de.teamlapen.vampirism.config;

import de.teamlapen.lib.lib.config.BalanceValues;
import de.teamlapen.lib.lib.config.DefaultDouble;
import de.teamlapen.lib.lib.config.DefaultInt;
import de.teamlapen.vampirism.VampirismMod;

import java.io.File;

public class BalanceEntityActions extends BalanceValues {

    @DefaultDouble(value = 0.3, minValue = 0, maxValue = 1, name = "Disruption Health Amount", comment = "Health points of maximum health that the entity must lose in order to interrupt the action")
    public double DISRUPTION_HEALTH_AMOUNT;

    /* Invisible Action */
    @DefaultInt(value = 7, minValue = 1, name = "Invisible Cooldown", comment = "In seconds")
    public int INVISIBLE_COOLDOWN;

    @DefaultInt(value = 4, minValue = 1, name = "Invisible Duration", comment = "In seconds")
    public int INVISIBLE_DURATION;

    /* Heal Action */
    @DefaultInt(value = 7, minValue = 1, name = "Invisible Cooldown", comment = "In seconds")
    public int HEAL_COOLDOWN;

    @DefaultInt(value = 30, minValue = 0, maxValue = 100, name = "Heal Amount", comment = "In percent")
    public int HEAL_AMOUNT;

    /* Regeneration Action */
    @DefaultInt(value = 5, minValue = 1, name = "Regeneration Duration", comment = "In seconds")
    public int REGENERATION_DURATION;

    @DefaultInt(value = 6, minValue = 1, name = "Regeneration Cooldown", comment = "In seconds")
    public int REGENERATION_COOLDOWN;

    @DefaultInt(value = 40, minValue = 0, maxValue = 100, name = "Regeneration Amount", comment = "In percent")
    public int REGENERATION_AMOUNT;

    /* Speed Action */
    @DefaultInt(value = 4, minValue = 1, name = "Speed Duration", comment = "In seconds")
    public int SPEED_DURATION;

    @DefaultInt(value = 6, minValue = 1, name = "Speed Cooldown", comment = "In seconds")
    public int SPEED_COOLDOWN;

    @DefaultDouble(value = 0.14, minValue = 0, name = "Speed Amount", comment = "Speed = basevalue * (1 + SPEED_AMOUNT)")
    public double SPEED_AMOUNT;

    /* Bat Spawn Action */
    @DefaultInt(value = 15, minValue = 1, name = "Batspawn Cooldown", comment = "In seconds")
    public int BATSPAWN_COOLDOWN;

    @DefaultInt(value = 4, minValue = 1, name = "Batspawn Amount", comment = "Bats to spawn")
    public int BATSPAWN_AMOUNT;

    /* Dark Projectile Action */
    @DefaultInt(value = 9, minValue = 1, name = "Dark Projectile Cooldown", comment = "In seconds")
    public int DARK_PROJECTILE_COOLDOWN;

    @DefaultDouble(value = 5, minValue = 0, name = "Dark Blood Projectile damage", comment = "Damage of the direct projectile hit")
    public double DARK_BLOOD_PROJECTILE_DAMAGE;

    @DefaultDouble(value = 2, minValue = 0, name = "Dark Blood Projectile indirect damage", comment = "Damage of the indirect projectile hit")
    public double DARK_BLOOD_PROJECTILE_INDIRECT_DAMAGE;

    /* Sunscreen Action */
    @DefaultInt(value = 8, minValue = 1, name = "Sunscreen Duration", comment = "In seconds")
    public int SUNSCREEN_DURATION;

    @DefaultInt(value = 10, minValue = 1, name = "Sunscreen Cooldown", comment = "In seconds")
    public int SUNSCREEN_COOLDOWN;

    /* Ignore SunDamage Action */
    @DefaultInt(value = 5, minValue = 1, name = "Ignore SunDamage Duration", comment = "In seconds")
    public int IGNORE_SUNDAMAGE_DURATION;

    @DefaultInt(value = 6, minValue = 1, name = "Ignore SunDamage Cooldown", comment = "In seconds")
    public int IGNORE_SUNDAMAGE_COOLDOWN;

    /* Garlic AOF Action */
    @DefaultInt(value = 5, minValue = 1, name = "Garlic AOF Duration", comment = "In seconds")
    public int GARLIC_DURATION;

    @DefaultInt(value = 5, minValue = 1, name = "Garlic AOF Cooldown", comment = "In seconds")
    public int GARLIC_COOLDOWN;

    /**
     * Creates a configuration for balance values
     *
     * @param directory
     */
    public BalanceEntityActions(File directory) {
        super("entity_actions", directory);
    }

    @Override
    protected boolean shouldUseAlternate() {
        return VampirismMod.isRealism();
    }

}

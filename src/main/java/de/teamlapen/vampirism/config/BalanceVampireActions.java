package de.teamlapen.vampirism.config;

import de.teamlapen.lib.lib.config.BalanceValues;
import de.teamlapen.lib.lib.config.DefaultBoolean;
import de.teamlapen.lib.lib.config.DefaultDouble;
import de.teamlapen.lib.lib.config.DefaultInt;
import de.teamlapen.vampirism.VampirismMod;

import java.io.File;

/**
 * Balance values for vampire players
 */
public class BalanceVampireActions extends BalanceValues {

    @DefaultInt(value = 60, minValue = 1, name = "Freeze cooldown", comment = "In seconds")
    public int FREEZE_COOLDOWN;

    @DefaultBoolean(value = true, name = "freeze_enabled")
    public boolean FREEZE_ENABLED;

    @DefaultInt(value = 6, minValue = 1, maxValue = 30, name = "Freeze duration", comment = "In seconds")
    public int FREEZE_DURATION;

    @DefaultInt(value = 10, name = "Invisibility (Vampire Lord) Duration", comment = "In seconds")
    public int INVISIBILITY_DURATION;
    @DefaultInt(value = 45, name = "Invisibility Cooldown", comment = "In seconds")
    public int INVISIBILITY_COOLDOWN;
    @DefaultBoolean(value = true, name = "invisibility_enabled")
    public boolean INVISIBILITY_ENABLED;

    @DefaultInt(value = 60, minValue = 0, name = "Regeneration Cool Down", comment = "In seconds")
    public int REGEN_COOLDOWN;
    @DefaultInt(value = 20, minValue = 0, name = "Regeneration Duration", comment = "In seconds")
    public int REGEN_DURATION;
    @DefaultBoolean(value = true, name = "regeneration_enabled")
    public boolean REGEN_ENABLED;

    @DefaultInt(value = 50, minValue = 1)
    public int TELEPORT_MAX_DISTANCE;

    @DefaultInt(value = 10, minValue = 1)
    public int TELEPORT_COOLDOWN;

    @DefaultBoolean(value = true, name = "teleport_enabled")
    public boolean TELEPORT_ENABLED;

    @DefaultInt(value = 20, minValue = 0, name = "Vampire Rage Cool Down", comment = "Vampire Rage cooldown duration")
    public int RAGE_COOLDOWN;
    @DefaultInt(value = 13, minValue = 1, name = "Vampire Rage Duration", comment = "Standard Vampire Rage duration")
    public int RAGE_MIN_DURATION;
    @DefaultInt(value = 5, minValue = 0, name = "Vampire Rage Duration Increase", comment = "Vampire Rage duration increase per level")
    public int RAGE_DUR_PL;
    @DefaultBoolean(value = true, name = "rage_enabled")
    public boolean RAGE_ENABLED;

    @DefaultDouble(value = 0.03, minValue = 0.01, maxValue = 0.05, comment = "Bat mode flying speed. Value for creative mode: 0.05")
    public double BAT_FLIGHT_SPEED;

    @DefaultInt(value = 1200, minValue = 0, comment = "Sunscreen action cooldown duration")
    public int SUNSCREEN_COOLDOWN;
    @DefaultInt(value = 30, minValue = 1, comment = "Sunscreen action duration")
    public int SUNSCREEN_DURATION;
    @DefaultBoolean(value = true, name = "sunscreen_enabled")
    public boolean SUNSCREEN_ENABLED;

    @DefaultBoolean(value = true, name = "bat_enabled")
    public boolean BAT_ENABLED;

    @DefaultInt(value = 0, minValue = 0, name = "bat_cooldown")
    public int BAT_COOLDOWN;

    @DefaultInt(value = Integer.MAX_VALUE, minValue = 10, name = "bat_duration")
    public int BAT_DURATION;


    @DefaultBoolean(value = true, name = "summon_bats_enabled")
    public boolean SUMMON_BAT_ENABLED;

    @DefaultInt(value = 300, minValue = 1, name = "summon_bats_ooldown")
    public int SUMMON_BAT_COOLDOWN;

    @DefaultInt(value = 16, minValue = 1, name = "summon_bats_count")
    public int SUMMON_BAT_COUNT;


    @DefaultInt(value = 30, comment = "In seconds")
    public int DISGUISE_DURATION;
    @DefaultBoolean(value = true, name = "disguise_enabled")
    public boolean DISGUISE_ENABLED;
    @DefaultInt(value = 60, minValue = 1, comment = "In seconds")
    public int DISGUISE_COOLDOWN;

    @DefaultBoolean(value = true)
    public boolean DARK_BLOOD_PROJECTILE_ENABLE;

    @DefaultInt(value = 4, minValue = 1, comment = "In seconds")
    public int DARK_BLOOD_PROJECTILE_COOLDOWN;

    @DefaultDouble(value = 6, minValue = 0, comment = "Damage of the direct projectile hit")
    public double DARK_BLOOD_PROJECTILE_DAMAGE;

    @DefaultDouble(value = 3, minValue = 0, comment = "Damage of the indirect projectile hit")
    public double DARK_BLOOD_PROJECTILE_INDIRECT_DAMAGE;

    @DefaultBoolean(value = true, name = "halfinvulnerable_enabled")
    public boolean HALFINVULNERABLE_ENABLE;
    @DefaultInt(value = 60, minValue = 0, name = "halfinvulnerable_cooldown")
    public int HALFINVULNERABLE_COOLDOWN;
    @DefaultInt(value = 30, minValue = 1, name = "halfinvulnerable_duration")
    public int HALFINVULNERABLE_DURATION;
    @DefaultDouble(value = 0.4D, minValue = 0.0D, comment = "damage threshold of max health for damage omitting")
    public double HALFINVULNERABLE_THRESHOLD;
    @DefaultInt(value = 4, minValue = 0, name = "halfinvulnerable_blood_costs")
    public int HALFINVULNERABLE_BLOOD_COSTS;

    /**
     * Creates a configuration for balance values
     *
     * @param directory
     */
    public BalanceVampireActions(File directory) {
        super("vampire_player_actions", directory);
    }

    @Override
    protected boolean shouldUseAlternate() {
        return VampirismMod.isRealism();
    }
}

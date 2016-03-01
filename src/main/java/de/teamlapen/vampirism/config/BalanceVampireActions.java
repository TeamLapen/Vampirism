package de.teamlapen.vampirism.config;

import de.teamlapen.lib.lib.config.BalanceValues;
import de.teamlapen.lib.lib.config.DefaultInt;
import de.teamlapen.vampirism.VampirismMod;

import java.io.File;

/**
 * Balance values for vampire players
 */
public class BalanceVampireActions extends BalanceValues {

    @DefaultInt(value = 60, minValue = 1, name = "Freeze cooldown", comment = "In seconds")
    public int FREEZE_COOLDOWN;

    @DefaultInt(value = 0, minValue = -1, maxValue = 0, name = "Freeze min level", comment = "Can only be -1 (disabled) or 0 (enabled). Only available for lords anyway.")
    public int FREEZE_MIN_LEVEL;

    @DefaultInt(value = 6, minValue = 1, maxValue = 30, name = "Freeze duration", comment = "In seconds")
    public int FREEZE_DURATION;

    @DefaultInt(value = 10, name = "Invisibility (Vampire Lord) Duration", comment = "In seconds")
    public int INVISIBILITY_DURATION;
    @DefaultInt(value = 45, name = "Invisibility Cooldown", comment = "In seconds")
    public int INVISIBILITY_COOLDOWN;
    @DefaultInt(value = 0, minValue = -1, maxValue = 0, name = "Invisibility min level", comment = "Can only be -1 (disabled) or 0 (enabled). Only available for lords anyway.")
    public int INVISIBILITY_MIN_LEVEL;

    @DefaultInt(value = 60, minValue = 0, name = "Regeneration Cool Down", comment = "In seconds")
    public int REGEN_COOLDOWN;
    @DefaultInt(value = 20, minValue = 0, name = "Regeneration Duration", comment = "In seconds")
    public int REGEN_DURATION;
    @DefaultInt(value = 4, minValue = -1, name = "Regeneration Min Level", comment = "Set to -1 to deactivate this skill")
    public int REGEN_MIN_LEVEL;

    @DefaultInt(value = 60, minValue = 1, name = "Lord teleport max distance")
    public int TELEPORT_MAX_DISTANCE;

    @DefaultInt(value = 15, minValue = 1, name = "Lord teleport cooldown")
    public int TELEPORT_COOLDOWN;

    @DefaultInt(value = 0, minValue = -1, maxValue = 0, name = "Teleport min level", comment = "Can only be -1 (disabled) or 0 (enabled")
    public int TELEPORT_MIN_LEVEL;

    @DefaultInt(value = 20, minValue = 0, name = "Vampire Rage Cool Down", comment = "Vampire Rage cooldown duration")
    public int RAGE_COOLDOWN;
    @DefaultInt(value = 10, minValue = 1, name = "Vampire Rage Duration", comment = "Standard Vampire Rage duration")
    public int RAGE_MIN_DURATION;
    @DefaultInt(value = 5, minValue = 0, name = "Vampire Rage Duration Increase", comment = "Vampire Rage duration increase per level")
    public int RAGE_DUR_PL;
    @DefaultInt(value = 8, minValue = -1, name = "Vampire Rage Min Level", comment = "Set to -1 to deactivate this skill")
    public int RAGE_MIN_LEVEL;

    @DefaultInt(value = 3, minValue = -1, name = "Bat Transformation Min Level")
    public int BAT_MIN_LEVEL;

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

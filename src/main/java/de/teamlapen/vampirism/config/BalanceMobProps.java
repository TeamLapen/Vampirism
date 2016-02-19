package de.teamlapen.vampirism.config;

import de.teamlapen.lib.lib.config.BalanceValues;
import de.teamlapen.lib.lib.config.DefaultDouble;
import de.teamlapen.lib.lib.config.DefaultInt;
import de.teamlapen.vampirism.VampirismMod;

import java.io.File;

/**
 * Balance values for Vampirism's mobs
 */
public class BalanceMobProps extends BalanceValues {

    @DefaultInt(value=600,name="blinding_bat_live_spawn",minValue = 1,comment = "Livespan in ticks")
    public int BLINDING_BAT_LIVE_SPAWN;
    @DefaultInt(value=40,name="blinding_bat_effect_duration",minValue = 1,comment = "Blinding duration in ticks")
    public int BLINDING_BAT_EFFECT_DURATION;
    @DefaultDouble(value = 5D,name = "ghost_attack_damage",minValue = 0)
    public double GHOST_ATTACK_DAMAGE;
    @DefaultInt(value = 14,name = "ghost_follow_range",minValue = 1,maxValue = 32)
    public int GHOST_FOLLOW_RANGE;
    @DefaultDouble(value = 0.2D,name = "ghost_speed",minValue = 0,maxValue = 2)
    public double GHOST_SPEED;
    @DefaultInt(value=30,name="ghost_health",minValue = 0)
    public int GHOST_HEALTH;

    @DefaultInt(value = 1, minValue = 0, name = "converted_mob_default_dmg")
    public int CONVERTED_MOB_DEFAULT_DMG;

    @DefaultInt(value = 60, comment = "Duration of the sanguinare effect for mobs in seconds", name = "sanguinare_avg_duration", minValue = 1)
    public int SANGUINARE_AVG_DURATION;

    @DefaultInt(value = 20, name = "blood_regen_chance", comment = "Probability that a bitten creature will regen one blood. Is checked every 2 seconds with a 1/n chance")
    public int BLOOD_REGEN_CHANCE;

    public BalanceMobProps(File directory) {
        super("mob_prop", directory);
    }

    @Override
    protected boolean shouldUseAlternate() {
        return VampirismMod.isRealism();
    }
}

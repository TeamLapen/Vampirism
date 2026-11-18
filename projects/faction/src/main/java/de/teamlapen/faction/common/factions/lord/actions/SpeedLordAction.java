package de.teamlapen.faction.common.factions.lord.actions;

import de.teamlapen.faction.api.factions.skills.ISkillPlayer;
import de.teamlapen.faction.common.config.FactionConfig;
import de.teamlapen.faction.common.core.FactionEffects;
import org.jetbrains.annotations.NotNull;

public class SpeedLordAction<T extends ISkillPlayer<T>> extends LordRangeEffectAction<T> {

    public SpeedLordAction() {
        super(FactionEffects.LORD_SPEED);
    }

    @Override
    public boolean isEnabled() {
        return FactionConfig.server().lordSpeedEnabled.get();
    }

    @Override
    public int getCooldown(@NotNull T player) {
        return super.getCooldown(player) + FactionConfig.server().lordSpeedCooldown.get();
    }

    @Override
    protected int getEffectDuration(T player) {
        return FactionConfig.server().lordSpeedDuration.get() * 20;
    }

    @Override
    protected int getEffectAmplifier(@NotNull T player) {
        return 0;
    }
}

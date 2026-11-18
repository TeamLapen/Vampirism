package de.teamlapen.faction.common.factions.lord.actions;

import de.teamlapen.faction.api.factions.skills.ISkillPlayer;
import de.teamlapen.faction.common.config.FactionConfig;
import de.teamlapen.faction.common.core.FactionEffects;
import org.jetbrains.annotations.NotNull;

public class AttackSpeedLordAction<T extends ISkillPlayer<T>> extends LordRangeEffectAction<T> {

    public AttackSpeedLordAction() {
        super(FactionEffects.LORD_ATTACK_SPEED);
    }

    @Override
    public boolean isEnabled() {
        return FactionConfig.server().lordAttackSpeedEnabled.get();
    }

    @Override
    public int getCooldown(@NotNull T player) {
        return super.getCooldown(player) + FactionConfig.server().lordAttackSpeedCooldown.get();
    }

    @Override
    protected int getEffectDuration(T player) {
        return FactionConfig.server().lordAttackSpeedDuration.get() * 20;
    }

    @Override
    protected int getEffectAmplifier(@NotNull T player) {
        return 0;
    }
}

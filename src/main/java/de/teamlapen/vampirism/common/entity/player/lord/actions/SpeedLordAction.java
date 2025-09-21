package de.teamlapen.vampirism.common.entity.player.lord.actions;

import de.teamlapen.vampirism.api.entity.player.ISkillPlayer;
import de.teamlapen.vampirism.common.config.ModConfig;
import de.teamlapen.vampirism.common.core.ModEffects;
import org.jetbrains.annotations.NotNull;

public class SpeedLordAction<T extends ISkillPlayer<T>> extends LordRangeEffectAction<T> {

    public SpeedLordAction() {
        super(ModEffects.LORD_SPEED);
    }

    @Override
    public boolean isEnabled() {
        return ModConfig.BALANCE.laLordSpeedEnabled.get();
    }

    @Override
    public int getCooldown(T player) {
        return super.getCooldown(player) + ModConfig.BALANCE.laLordSpeedCooldown.get();
    }

    @Override
    protected int getEffectDuration(T player) {
        return ModConfig.BALANCE.laLordSpeedDuration.get() * 20;
    }

    @Override
    protected int getEffectAmplifier(@NotNull T player) {
        return 0;
    }
}

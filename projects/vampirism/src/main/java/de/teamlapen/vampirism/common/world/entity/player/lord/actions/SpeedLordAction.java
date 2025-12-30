package de.teamlapen.vampirism.common.world.entity.player.lord.actions;

import de.teamlapen.faction.api.factions.skills.ISkillPlayer;
import de.teamlapen.vampirism.common.config.ModConfig;
import de.teamlapen.vampirism.common.core.ModEffects;
import org.jetbrains.annotations.NotNull;

public class SpeedLordAction<T extends ISkillPlayer<T>> extends LordRangeEffectAction<T> {

    public SpeedLordAction() {
        super(ModEffects.LORD_SPEED);
    }

    @Override
    public boolean isEnabled() {
        return ModConfig.balance().laLordSpeedEnabled.get();
    }

    @Override
    public int getCooldown(@NotNull T player) {
        return super.getCooldown(player) + ModConfig.balance().laLordSpeedCooldown.get();
    }

    @Override
    protected int getEffectDuration(T player) {
        return ModConfig.balance().laLordSpeedDuration.get() * 20;
    }

    @Override
    protected int getEffectAmplifier(@NotNull T player) {
        return 0;
    }
}

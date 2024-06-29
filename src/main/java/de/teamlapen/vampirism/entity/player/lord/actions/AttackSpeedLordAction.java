package de.teamlapen.vampirism.entity.player.lord.actions;

import de.teamlapen.vampirism.api.entity.player.ISkillPlayer;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModEffects;
import org.jetbrains.annotations.NotNull;

public class AttackSpeedLordAction<T extends ISkillPlayer<T>> extends LordRangeEffectAction<T> {

    public AttackSpeedLordAction() {
        super(ModEffects.LORD_ATTACK_SPEED);
    }

    @Override
    public boolean isEnabled() {
        return VampirismConfig.BALANCE.laLordAttackSpeedEnabled.get();
    }

    @Override
    public int getCooldown(T player) {
        return super.getCooldown(player) + VampirismConfig.BALANCE.laLordAttackSpeedCooldown.get();
    }

    @Override
    protected int getEffectDuration(T player) {
        return VampirismConfig.BALANCE.laLordAttackSpeedDuration.get() * 20;
    }

    @Override
    protected int getEffectAmplifier(@NotNull T player) {
        return 0;
    }
}

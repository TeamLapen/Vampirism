package de.teamlapen.vampirism.common.world.entity.player.lord.actions;

import de.teamlapen.factions.api.factions.skills.ISkillPlayer;
import de.teamlapen.vampirism.common.config.ModConfig;
import de.teamlapen.vampirism.common.core.ModEffects;
import org.jetbrains.annotations.NotNull;

public class AttackSpeedLordAction<T extends ISkillPlayer<T>> extends LordRangeEffectAction<T> {

    public AttackSpeedLordAction() {
        super(ModEffects.LORD_ATTACK_SPEED);
    }

    @Override
    public boolean isEnabled() {
        return ModConfig.BALANCE.laLordAttackSpeedEnabled.get();
    }

    @Override
    public int getCooldown(@NotNull T player) {
        return super.getCooldown(player) + ModConfig.BALANCE.laLordAttackSpeedCooldown.get();
    }

    @Override
    protected int getEffectDuration(T player) {
        return ModConfig.BALANCE.laLordAttackSpeedDuration.get() * 20;
    }

    @Override
    protected int getEffectAmplifier(@NotNull T player) {
        return 0;
    }
}

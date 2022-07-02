package de.teamlapen.vampirism.player.actions;

import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModEffects;

public class AttackSpeedLordAction<T extends IFactionPlayer> extends LordRangeEffectAction<T> {

    public AttackSpeedLordAction(IPlayableFaction faction) {
        super(ModEffects.LORD_ATTACK_SPEED, faction);
    }

    @Override
    public boolean isEnabled() {
        return VampirismConfig.BALANCE.laLordAttackSpeedEnabled.get();
    }

    @Override
    public int getCooldown() {
        return VampirismConfig.BALANCE.laLordAttackSpeedCooldown.get();
    }

    @Override
    public int getCooldown(IFactionPlayer player) {
        return super.getCooldown(player) + VampirismConfig.BALANCE.laLordAttackSpeedCooldown.get();
    }

    @Override
    protected int getEffectDuration(IFactionPlayer player) {
        return VampirismConfig.BALANCE.laLordAttackSpeedDuration.get() * 20;
    }

    @Override
    protected int getEffectAmplifier(IFactionPlayer player) {
        return 0;
    }
}

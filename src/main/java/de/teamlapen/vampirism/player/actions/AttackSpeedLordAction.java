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
        return VampirismConfig.BALANCE.faLordSpeedEnabled.get();
    }

    @Override
    public int getCooldown() {
        return VampirismConfig.BALANCE.faLordSpeedCooldown.get();
    }

    @Override
    public int getCooldown(IFactionPlayer player) {
        return super.getCooldown(player) + VampirismConfig.BALANCE.faLordSpeedCooldown.get();
    }

    @Override
    protected int getEffectDuration(IFactionPlayer player) {
        return VampirismConfig.BALANCE.faLordSpeedDuration.get() * 20;
    }
}

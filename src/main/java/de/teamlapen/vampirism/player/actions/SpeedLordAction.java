package de.teamlapen.vampirism.player.actions;

import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModEffects;

public class SpeedLordAction<T extends IFactionPlayer<T>> extends LordRangeEffectAction<T> {

    public SpeedLordAction(IPlayableFaction<T> faction) {
        super(ModEffects.LORD_SPEED, faction);
    }

    @Override
    public boolean isEnabled() {
        return VampirismConfig.BALANCE.laLordSpeedEnabled.get();
    }

    @Override
    public int getCooldown(T player) {
        return super.getCooldown(player) + VampirismConfig.BALANCE.laLordSpeedCooldown.get();
    }

    @Override
    protected int getEffectDuration(T player) {
        return VampirismConfig.BALANCE.laLordSpeedDuration.get() * 20;
    }

    @Override
    protected int getEffectAmplifier(T player) {
        return 0;
    }
}

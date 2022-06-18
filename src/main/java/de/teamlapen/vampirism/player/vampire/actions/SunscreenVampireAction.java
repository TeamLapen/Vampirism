package de.teamlapen.vampirism.player.vampire.actions;

import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.actions.ILastingAction;
import de.teamlapen.vampirism.api.entity.player.vampire.DefaultVampireAction;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModEffects;
import de.teamlapen.vampirism.core.ModRefinements;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectInstance;

/**
 * Adds sunscreen
 */
public class SunscreenVampireAction extends DefaultVampireAction implements ILastingAction<IVampirePlayer> {

    @Override
    public boolean activate(IVampirePlayer vampire) {
        addEffectInstance(vampire, new EffectInstance(ModEffects.SUNSCREEN.get(), getDuration(vampire), 3, false, false));
        return true;
    }

    @Override
    public int getCooldown() {
        return VampirismConfig.BALANCE.vaSunscreenCooldown.get() * 20;
    }

    @Override
    public int getDuration(int level) {
        return 20 * (VampirismConfig.BALANCE.vaSunscreenDuration.get());
    }

    @Override
    public int getDuration(IFactionPlayer player) {
        int duration = 20 * (VampirismConfig.BALANCE.vaSunscreenDuration.get());
        if (player.getSkillHandler().isRefinementEquipped(ModRefinements.SUN_SCREEN.get())) {
            duration *= VampirismConfig.BALANCE.vrSunscreenDurationMod.get();
        }
        return duration;
    }

    @Override
    public boolean isEnabled() {
        return VampirismConfig.BALANCE.vaSunscreenEnabled.get();
    }

    @Override
    public void onActivatedClient(IVampirePlayer vampire) {

    }

    @Override
    public void onDeactivated(IVampirePlayer vampire) {
        removePotionEffect(vampire, ModEffects.SUNSCREEN.get());
    }

    @Override
    public void onReActivated(IVampirePlayer vampire) {

    }

    @Override
    public boolean onUpdate(IVampirePlayer vampire) {
        return false;
    }

    @Override
    public boolean showHudCooldown(PlayerEntity player) {
        return true;
    }

    @Override
    public boolean showHudDuration(PlayerEntity player) {
        return true;
    }
}

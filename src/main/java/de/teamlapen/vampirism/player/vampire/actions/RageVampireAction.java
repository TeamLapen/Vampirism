package de.teamlapen.vampirism.player.vampire.actions;

import de.teamlapen.vampirism.api.entity.player.actions.ILastingAction;
import de.teamlapen.vampirism.api.entity.player.vampire.DefaultVampireAction;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.config.VampirismConfig;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;

public class RageVampireAction extends DefaultVampireAction implements ILastingAction<IVampirePlayer> {

    public RageVampireAction() {
        super();
    }

    @Override
    public boolean activate(IVampirePlayer vampire) {
        int duration = getDuration(vampire);
        addEffectInstance(vampire, new EffectInstance(Effects.MOVEMENT_SPEED, duration, 2, false, false));
        addEffectInstance(vampire, new EffectInstance(Effects.DAMAGE_BOOST, duration, 0, false, false));
        addEffectInstance(vampire, new EffectInstance(Effects.DIG_SPEED, duration, 0, false, false));

        return true;
    }

    @Override
    public boolean canBeUsedBy(IVampirePlayer vampire) {
        return !vampire.getActionHandler().isActionActive(VampireActions.BAT.get());
    }

    @Override
    public int getCooldown() {
        return VampirismConfig.BALANCE.vaRageCooldown.get() * 20;
    }

    @Override
    public int getDuration(int level) {
        return 20 * (VampirismConfig.BALANCE.vaRageMinDuration.get() + VampirismConfig.BALANCE.vaRageDurationIncrease.get() * level);
    }

    @Override
    public boolean isEnabled() {
        return VampirismConfig.BALANCE.vaRageEnabled.get();
    }

    @Override
    public void onActivatedClient(IVampirePlayer vampire) {

    }

    @Override
    public void onDeactivated(IVampirePlayer vampire) {
        removePotionEffect(vampire, Effects.MOVEMENT_SPEED);
        removePotionEffect(vampire, Effects.DAMAGE_BOOST);
        removePotionEffect(vampire, Effects.DIG_SPEED);
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

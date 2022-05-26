package de.teamlapen.vampirism.player.vampire.actions;

import de.teamlapen.vampirism.api.entity.player.actions.ILastingAction;
import de.teamlapen.vampirism.api.entity.player.vampire.DefaultVampireAction;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.config.VampirismConfig;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public class RageVampireAction extends DefaultVampireAction implements ILastingAction<IVampirePlayer> {

    public RageVampireAction() {
        super();
    }

    @Override
    public boolean activate(IVampirePlayer vampire) {
        int duration = getDuration(vampire);
        addEffectInstance(vampire, new MobEffectInstance(MobEffects.MOVEMENT_SPEED, duration, 2, false, false));
        addEffectInstance(vampire, new MobEffectInstance(MobEffects.DAMAGE_BOOST, duration, 0, false, false));
        addEffectInstance(vampire, new MobEffectInstance(MobEffects.DIG_SPEED, duration, 0, false, false));

        return true;
    }

    @Override
    public boolean canBeUsedBy(IVampirePlayer vampire) {
        return !vampire.getActionHandler().isActionActive(VampireActions.bat.get());
    }

    @Override
    public int getCooldown(IVampirePlayer player) {
        return VampirismConfig.BALANCE.vaRageCooldown.get() * 20;
    }

    @Override
    public int getDuration(IVampirePlayer player) {
        return 20 * (VampirismConfig.BALANCE.vaRageMinDuration.get() + VampirismConfig.BALANCE.vaRageDurationIncrease.get() * player.getLevel());
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
        removePotionEffect(vampire, MobEffects.MOVEMENT_SPEED);
        removePotionEffect(vampire, MobEffects.DAMAGE_BOOST);
        removePotionEffect(vampire, MobEffects.DIG_SPEED);
    }

    @Override
    public void onReActivated(IVampirePlayer vampire) {

    }

    @Override
    public boolean onUpdate(IVampirePlayer vampire) {
        return false;
    }
}

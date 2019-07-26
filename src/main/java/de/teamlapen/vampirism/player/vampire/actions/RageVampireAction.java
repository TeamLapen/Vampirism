package de.teamlapen.vampirism.player.vampire.actions;

import de.teamlapen.vampirism.api.entity.player.actions.ILastingAction;
import de.teamlapen.vampirism.api.entity.player.vampire.DefaultVampireAction;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.config.Balance;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;

public class RageVampireAction extends DefaultVampireAction implements ILastingAction<IVampirePlayer> {

    public RageVampireAction() {
        super();
    }

    @Override
    public boolean activate(IVampirePlayer vampire) {
        vampire.getRepresentingPlayer().addPotionEffect(new EffectInstance(Effects.SPEED, getDuration(vampire.getLevel()), 2, false, false));
        vampire.getRepresentingPlayer().addPotionEffect(new EffectInstance(Effects.STRENGTH, getDuration(vampire.getLevel()), 0, false, false));
        return true;
    }

    @Override
    public boolean canBeUsedBy(IVampirePlayer vampire) {
        return !vampire.getActionHandler().isActionActive(VampireActions.bat);
    }

    @Override
    public int getCooldown() {
        return Balance.vpa.RAGE_COOLDOWN * 20;
    }

    @Override
    public int getDuration(int level) {
        return 20 * (Balance.vpa.RAGE_MIN_DURATION + Balance.vpa.RAGE_DUR_PL);
    }

    @Override
    public boolean isEnabled() {
        return Balance.vpa.RAGE_ENABLED;
    }

    @Override
    public void onActivatedClient(IVampirePlayer vampire) {

    }

    @Override
    public void onDeactivated(IVampirePlayer vampire) {
        vampire.getRepresentingPlayer().removePotionEffect(Effects.SPEED);
        vampire.getRepresentingPlayer().removePotionEffect(Effects.STRENGTH);
    }

    @Override
    public void onReActivated(IVampirePlayer vampire) {

    }

    @Override
    public boolean onUpdate(IVampirePlayer vampire) {
        return false;
    }
}

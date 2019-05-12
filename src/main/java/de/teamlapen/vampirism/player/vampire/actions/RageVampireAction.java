package de.teamlapen.vampirism.player.vampire.actions;

import de.teamlapen.vampirism.api.entity.player.actions.ILastingAction;
import de.teamlapen.vampirism.api.entity.player.vampire.DefaultVampireAction;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.config.Balance;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;

public class RageVampireAction extends DefaultVampireAction implements ILastingAction<IVampirePlayer> {

    public RageVampireAction() {
        super(null);
    }

    @Override
    public boolean activate(IVampirePlayer vampire) {
        vampire.getRepresentingPlayer().addPotionEffect(new PotionEffect(MobEffects.SPEED, getDuration(vampire.getLevel()), 2, false, false));
        vampire.getRepresentingPlayer().addPotionEffect(new PotionEffect(MobEffects.STRENGTH, getDuration(vampire.getLevel()), 0, false, false));
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
    public int getMinU() {
        return 32;
    }

    @Override
    public int getMinV() {
        return 0;
    }

    @Override
    public String getTranslationKey() {
        return "action.vampirism.vampire.vampire_rage";
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
        vampire.getRepresentingPlayer().removePotionEffect(MobEffects.SPEED);
        vampire.getRepresentingPlayer().removePotionEffect(MobEffects.STRENGTH);
    }

    @Override
    public void onReActivated(IVampirePlayer vampire) {

    }

    @Override
    public boolean onUpdate(IVampirePlayer vampire) {
        return false;
    }
}

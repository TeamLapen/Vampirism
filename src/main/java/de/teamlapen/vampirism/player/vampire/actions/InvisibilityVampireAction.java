package de.teamlapen.vampirism.player.vampire.actions;

import de.teamlapen.vampirism.api.entity.player.actions.ILastingAction;
import de.teamlapen.vampirism.api.entity.player.vampire.DefaultVampireAction;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.config.VampirismConfig;


public class InvisibilityVampireAction extends DefaultVampireAction implements ILastingAction<IVampirePlayer> {
    public InvisibilityVampireAction() {
        super();
    }

    @Override
    public boolean activate(IVampirePlayer vampire) {
        vampire.getRepresentingPlayer().setInvisible(true);
        return true;
    }

    @Override
    public int getCooldown() {
        return VampirismConfig.BALANCE.vaInvisibilityCooldown.get() * 20;
    }

    @Override
    public int getDuration(int level) {
        return VampirismConfig.BALANCE.vaInvisibilityDuration.get() * 20;
    }

    @Override
    public boolean isEnabled() {
        return VampirismConfig.BALANCE.vaInvisibilityEnabled.get();
    }

    @Override
    public void onActivatedClient(IVampirePlayer vampire) {

    }

    @Override
    public void onDeactivated(IVampirePlayer vampire) {
        vampire.getRepresentingPlayer().setInvisible(false);
    }

    @Override
    public void onReActivated(IVampirePlayer vampire) {
        activate(vampire);
    }

    @Override
    public boolean onUpdate(IVampirePlayer vampire) {
        if (!vampire.getRepresentingPlayer().isInvisible()) {
            vampire.getRepresentingPlayer().setInvisible(true);
        }
        return false;
    }
}

package de.teamlapen.vampirism.player.vampire.actions;

import de.teamlapen.vampirism.api.entity.player.actions.ILastingAction;
import de.teamlapen.vampirism.api.entity.player.vampire.DefaultVampireAction;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.config.Balance;
import de.teamlapen.vampirism.player.vampire.VampirePlayer;

/**
 * Disguise skill
 */
public class DisguiseVampireAction extends DefaultVampireAction implements ILastingAction<IVampirePlayer> {

    public DisguiseVampireAction() {
        super(null);
    }

    @Override
    public boolean activate(IVampirePlayer player) {
        ((VampirePlayer) player).getSpecialAttributes().disguised = true;
        ((VampirePlayer) player).getSpecialAttributes().disguisedAs = null;
        player.getRepresentingPlayer().refreshDisplayName();
        return true;
    }

    @Override
    public int getCooldown() {
        return Balance.vpa.DISGUISE_COOLDOWN * 20;
    }

    @Override
    public int getDuration(int level) {
        return Balance.vpa.DISGUISE_DURATION * 20;
    }

    @Override
    public int getMinU() {
        return 160;
    }

    @Override
    public int getMinV() {
        return 0;
    }

    @Override
    public String getUnlocalizedName() {
        return "action.vampirism.vampire.disguise";
    }

    @Override
    public boolean isEnabled() {
        return Balance.vpa.DISGUISE_ENABLED;
    }

    @Override
    public void onActivatedClient(IVampirePlayer player) {
        activate(player);
    }

    @Override
    public void onDeactivated(IVampirePlayer player) {
        ((VampirePlayer) player).getSpecialAttributes().disguised = false;
        player.getRepresentingPlayer().refreshDisplayName();
    }

    @Override
    public void onReActivated(IVampirePlayer player) {
        activate(player);
    }

    @Override
    public boolean onUpdate(IVampirePlayer player) {
        return false;
    }
}

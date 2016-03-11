package de.teamlapen.vampirism.entity.player.vampire.actions;

import de.teamlapen.vampirism.api.entity.player.actions.ILastingAction;
import de.teamlapen.vampirism.api.entity.player.vampire.DefaultVampireAction;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.config.Balance;
import de.teamlapen.vampirism.entity.player.vampire.VampirePlayer;

/**
 * Disguise skill
 */
public class DisguiseVampireAction extends DefaultVampireAction implements ILastingAction<IVampirePlayer> {

    public DisguiseVampireAction() {
        super(null);
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
        return "skill.vampirism.disguise";
    }

    @Override
    public boolean isEnabled() {
        return Balance.vpa.DISGUISE_ENABLED;
    }

    @Override
    public boolean onActivated(IVampirePlayer player) {
        ((VampirePlayer) player).getSpecialAttributes().disguised = true;
        return true;
    }

    @Override
    public void onActivatedClient(IVampirePlayer player) {
        onActivated(player);
    }

    @Override
    public void onDeactivated(IVampirePlayer player) {
        ((VampirePlayer) player).getSpecialAttributes().disguised = false;
    }

    @Override
    public void onReActivated(IVampirePlayer player) {
        onActivated(player);
    }

    @Override
    public boolean onUpdate(IVampirePlayer player) {
        return false;
    }
}

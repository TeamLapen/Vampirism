/**
 * 
 */
package de.teamlapen.vampirism.player.vampire.actions;

import de.teamlapen.vampirism.api.entity.player.actions.ILastingAction;
import de.teamlapen.vampirism.api.entity.player.vampire.DefaultVampireAction;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.config.Balance;
import de.teamlapen.vampirism.player.vampire.VampirePlayer;

/**
 * @author cheaterpaul
 *
 */
public class HalfInvulnerableAction extends DefaultVampireAction implements ILastingAction<IVampirePlayer>{

    public HalfInvulnerableAction() {
        super(null);
    }

    @Override
    public int getCooldown() {
        return Balance.vpa.HALFINVULNERABLE_COOLDOWN;
    }

    @Override
    public int getMinU() {

        return 208;
    }

    @Override
    public int getMinV() {

        return 0;
    }

    @Override
    public String getUnlocalizedName() {

        return "action.vampirism.vampire.halfinvulnerable_skill";
    }

    @Override
    public boolean isEnabled() {
        return Balance.vpa.HALFINVULNERABLE_ENABLE;
    }

    @Override
    protected boolean activate(IVampirePlayer playerIn) {
        VampirePlayer player = VampirePlayer.get(playerIn.getRepresentingPlayer());
        player.getSpecialAttributes().half_invulnerable = true;
        return true;
    }

    @Override
    public int getDuration(int level) {
        return 20 * (Balance.vpa.HALFINVULNERABLE_DURATION);
    }

    @Override
    public void onActivatedClient(IVampirePlayer player) {
    }

    @Override
    public void onDeactivated(IVampirePlayer playerIn) {
        VampirePlayer player = VampirePlayer.get(playerIn.getRepresentingPlayer());
        player.getSpecialAttributes().half_invulnerable = false;
    }

    @Override
    public void onReActivated(IVampirePlayer playerIn) {
        VampirePlayer player = VampirePlayer.get(playerIn.getRepresentingPlayer());
        player.getSpecialAttributes().half_invulnerable = true;
    }

    @Override
    public boolean onUpdate(IVampirePlayer player) {
        return false;
    }

}

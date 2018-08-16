/**
 * 
 */
package de.teamlapen.vampirism.player.vampire.actions;

import de.teamlapen.vampirism.api.entity.player.actions.ILastingAction;
import de.teamlapen.vampirism.api.entity.player.vampire.DefaultVampireAction;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.config.Balance;
import de.teamlapen.vampirism.player.vampire.VampirePlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;

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
        return 20 * (Balance.vpa.HALFINVULNERABLE_COOLDOWN);
    }

    @Override
    public int getDuration(int level) {
        return 20 * (Balance.vpa.HALFINVULNERABLE_DURATION);
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
    public void onActivatedClient(IVampirePlayer player) {
    }

    @Override
    public void onDeactivated(IVampirePlayer player) {
        ((VampirePlayer) player).getSpecialAttributes().half_invulnerable = false;
    }

    @Override
    public void onReActivated(IVampirePlayer player) {
        ((VampirePlayer) player).getSpecialAttributes().half_invulnerable = true;

    }

    @Override
    public boolean onUpdate(IVampirePlayer player) {
        return false;
    }

    @Override
    protected boolean activate(IVampirePlayer playerIn) {
        ((VampirePlayer) playerIn).getSpecialAttributes().half_invulnerable = true;
        playerIn.getRepresentingPlayer().addPotionEffect(
                new PotionEffect(MobEffects.SLOWNESS, getDuration(playerIn.getLevel()), 1, false, false));
        return true;
    }

}

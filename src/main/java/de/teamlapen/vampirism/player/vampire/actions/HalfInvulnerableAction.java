/**
 * 
 */
package de.teamlapen.vampirism.player.vampire.actions;

import de.teamlapen.vampirism.api.entity.player.actions.ILastingAction;
import de.teamlapen.vampirism.api.entity.player.vampire.DefaultVampireAction;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.config.Balance;
import de.teamlapen.vampirism.player.vampire.VampirePlayer;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;

/**
 * @author cheaterpaul
 *
 */
public class HalfInvulnerableAction extends DefaultVampireAction implements ILastingAction<IVampirePlayer>{

    public HalfInvulnerableAction() {
        super();
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
    public boolean isEnabled() {
        return Balance.vpa.HALFINVULNERABLE_ENABLE;
    }

    @Override
    public void onActivatedClient(IVampirePlayer player) {
    }

    @Override
    public void onDeactivated(IVampirePlayer player) {
        ((VampirePlayer) player).getSpecialAttributes().half_invulnerable = false;
        /*
         * We have a difficult situation here.
         * We want to remove the slowness effect this skill adds if the skill is terminated prematurely, but at the same time we do not want to make this a way to get rid of long lasting slowness effects added by other things.
         * Since we cannot determine what added what, current solution is to only remove the potion effect if the remaining duration is shorter than the maximum duration of this skill.
         * It is not ideal because it might seem somewhat inconsistent to the player, but at least it definitively removes the slowness effect added by this skill and at the same time does not allow to cancel long lasting effects.
         */
        if (player.getRepresentingPlayer().isPotionActive(Effects.SLOWNESS)) {
            if (player.getRepresentingPlayer().getActivePotionEffect(Effects.SLOWNESS).getDuration() < getDuration(player.getLevel())) {
                player.getRepresentingPlayer().removePotionEffect(Effects.SLOWNESS);
            }
        }
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
                new EffectInstance(Effects.SLOWNESS, getDuration(playerIn.getLevel()) - 1, 1, false, false));
        return true;
    }

}

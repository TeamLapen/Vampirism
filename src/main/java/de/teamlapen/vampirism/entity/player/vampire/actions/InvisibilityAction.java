package de.teamlapen.vampirism.entity.player.vampire.actions;

import de.teamlapen.vampirism.api.entity.player.vampire.DefaultAction;
import de.teamlapen.vampirism.api.entity.player.vampire.ILastingVampireAction;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.config.Balance;


public class InvisibilityAction extends DefaultAction implements ILastingVampireAction {
    public InvisibilityAction() {
        super(null);
    }
//      TODO activate again
//    @Override
//    public boolean canBeUsedBy(IVampirePlayer vampire) {
//        return vampire.isVampireLord();
//    }

    @Override
    public int getCooldown() {
        return Balance.vps.INVISIBILITY_COOLDOWN * 20;
    }

    @Override
    public int getDuration(int level) {
        return Balance.vps.INVISIBILITY_DURATION * 20;
    }

    @Override
    public int getMinLevel() {
        return Balance.vps.INVISIBILITY_MIN_LEVEL;
    }

    @Override
    public int getMinU() {
        return 128;
    }

    @Override
    public int getMinV() {
        return 0;
    }

    @Override
    public String getUnlocalizedName() {
        return "skill.vampirism.invisibility";
    }

    @Override
    public boolean onActivated(IVampirePlayer vampire) {
        vampire.getRepresentingPlayer().setInvisible(true);
        return true;
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
        onActivated(vampire);
    }

    @Override
    public boolean onUpdate(IVampirePlayer vampire) {
        if (!vampire.getRepresentingPlayer().isInvisible()) {
            vampire.getRepresentingPlayer().setInvisible(true);
        }
        return false;
    }
}

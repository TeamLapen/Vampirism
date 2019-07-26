package de.teamlapen.vampirism.player.hunter.actions;

import de.teamlapen.vampirism.api.entity.player.actions.ILastingAction;
import de.teamlapen.vampirism.api.entity.player.hunter.DefaultHunterAction;
import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.config.Balance;
import de.teamlapen.vampirism.player.hunter.HunterPlayer;

/**
 * Allows a hunter player to disguise himself which makes him less visible and reduces the detection radius for mobs
 */
public class DisguiseHunterAction extends DefaultHunterAction implements ILastingAction<IHunterPlayer> {
    public static final int FADE_TICKS = 20;

    public DisguiseHunterAction() {
        super();
    }

    @Override
    public boolean activate(IHunterPlayer player) {
        ((HunterPlayer) player).getSpecialAttributes().activateDisguise();
        return true;
    }

    @Override
    public int getCooldown() {
        return 0;
    }

    @Override
    public int getDuration(int level) {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isEnabled() {
        return Balance.hpa.DISGUISE_ENABLED;
    }

    @Override
    public void onActivatedClient(IHunterPlayer player) {
        ((HunterPlayer) player).getSpecialAttributes().activateDisguise();

    }

    @Override
    public void onDeactivated(IHunterPlayer player) {
        ((HunterPlayer) player).getSpecialAttributes().resetDisguise();

    }

    @Override
    public void onReActivated(IHunterPlayer player) {
        ((HunterPlayer) player).getSpecialAttributes().activateDisguise();

    }

    @Override
    public boolean onUpdate(IHunterPlayer player) {
        ((HunterPlayer) player).getSpecialAttributes().increaseDisguiseTicks();
        return false;
    }

    @Override
    public boolean canBeUsedBy(IHunterPlayer player) {
        return !player.getActionHandler().isActionActive(HunterActions.awareness_hunter);
    }
}

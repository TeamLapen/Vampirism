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
        super(null);
    }

    @Override
    public int getCooldown() {
        return Balance.hpa.DISGUISE_COOLDOWN * 20;
    }

    @Override
    public int getDuration(int level) {
        return Balance.hpa.DISGUISE_DURATION * 20;
    }

    @Override
    public int getMinU() {
        return 0;
    }

    @Override
    public int getMinV() {
        return 0;
    }

    @Override
    public String getUnlocalizedName() {
        return "action.vampirism.hunter.disguise";
    }

    @Override
    public boolean isEnabled() {
        return Balance.hpa.DISGUISE_ENABLED;
    }

    @Override
    public boolean onActivated(IHunterPlayer player) {
        ((HunterPlayer) player).getSpecialAttributes().activateDisguise();
        return true;
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
}

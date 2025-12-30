package de.teamlapen.vampirism.common.world.entity.player.hunter.actions;

import de.teamlapen.faction.api.factions.actions.IActionResult;
import de.teamlapen.faction.api.factions.actions.ILastingAction;
import de.teamlapen.vampirism.api.world.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.common.config.ModConfig;
import de.teamlapen.vampirism.common.world.entity.player.hunter.HunterPlayer;
import net.minecraft.world.entity.player.Player;

/**
 * Allows a hunter player to disguise himself which makes him less visible and reduces the detection radius for mobs
 */
public class DisguiseHunterAction extends DefaultHunterAction implements ILastingAction<IHunterPlayer> {
    public static final int FADE_TICKS = 20;

    public DisguiseHunterAction() {
        super();
    }

    @Override
    public IActionResult activate(IHunterPlayer player, ActivationContext context) {
        ((HunterPlayer) player).getSpecialAttributes().activateConcealment();
        return IActionResult.SUCCESS;
    }

    @Override
    public IActionResult canBeUsedBy(IHunterPlayer player) {
        return IActionResult.otherAction(player.getActionHandler(), HunterActions.AWARENESS_HUNTER);
    }

    @Override
    public int getCooldown(IHunterPlayer player) {
        return 0;
    }

    @Override
    public int getDuration(IHunterPlayer player) {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isEnabled() {
        return ModConfig.balance().haDisguiseEnabled.get();
    }

    @Override
    public void onActivatedClient(IHunterPlayer player) {
        ((HunterPlayer) player).getSpecialAttributes().activateConcealment();

    }

    @Override
    public void onDeactivated(IHunterPlayer player) {
        ((HunterPlayer) player).getSpecialAttributes().resetConcealment();

    }

    @Override
    public void onReActivated(IHunterPlayer player) {
        ((HunterPlayer) player).getSpecialAttributes().activateConcealment();

    }

    @Override
    public boolean onUpdate(IHunterPlayer player) {
        ((HunterPlayer) player).getSpecialAttributes().increaseConcealmentTicks();
        return false;
    }

    @Override
    public boolean showHudDuration(Player player) {
        return true;
    }
}

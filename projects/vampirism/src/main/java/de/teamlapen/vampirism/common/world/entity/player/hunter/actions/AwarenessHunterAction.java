package de.teamlapen.vampirism.common.world.entity.player.hunter.actions;

import de.teamlapen.factions.api.factions.actions.IActionResult;
import de.teamlapen.factions.api.factions.actions.ILastingAction;
import de.teamlapen.vampirism.api.world.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.common.config.ModConfig;
import net.minecraft.world.entity.player.Player;

public class AwarenessHunterAction extends DefaultHunterAction implements ILastingAction<IHunterPlayer> {

    public AwarenessHunterAction() {
        super();
    }

    @Override
    public IActionResult canBeUsedBy(IHunterPlayer player) {
        return IActionResult.otherAction(player.getActionHandler(), HunterActions.DISGUISE_HUNTER);
    }

    @Override
    public int getCooldown(IHunterPlayer player) {
        return ModConfig.balance().haAwarenessCooldown.get();
    }

    @Override
    public int getDuration(IHunterPlayer player) {
        return ModConfig.balance().haAwarenessDuration.get();
    }

    @Override
    public boolean isEnabled() {
        return ModConfig.balance().haAwarenessEnabled.get();
    }

    @Override
    public void onActivatedClient(IHunterPlayer player) {
        onUpdate(player);
    }

    @Override
    public void onDeactivated(IHunterPlayer player) {
    }

    @Override
    public void onReActivated(IHunterPlayer player) {
        onUpdate(player);
    }

    @Override
    protected IActionResult activate(IHunterPlayer player, ActivationContext context) {
        return IActionResult.SUCCESS;
    }

    @Override
    public boolean showHudCooldown(Player player) {
        return true;
    }

    @Override
    public boolean showHudDuration(Player player) {
        return true;
    }

}
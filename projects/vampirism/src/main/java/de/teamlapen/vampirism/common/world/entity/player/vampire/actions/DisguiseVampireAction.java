package de.teamlapen.vampirism.common.world.entity.player.vampire.actions;

import de.teamlapen.faction.api.factions.actions.IActionResult;
import de.teamlapen.faction.api.factions.actions.ILastingAction;
import de.teamlapen.faction.common.core.DefaultFactions;
import de.teamlapen.vampirism.api.world.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.common.config.ModConfig;
import net.minecraft.world.entity.player.Player;

/**
 * Disguise skill
 */
public class DisguiseVampireAction extends DefaultVampireAction implements ILastingAction<IVampirePlayer> {

    public DisguiseVampireAction() {
        super();
    }

    @Override
    public IActionResult activate(IVampirePlayer player, ActivationContext context) {
        activate(player);
        return IActionResult.SUCCESS;
    }

    protected void activate(IVampirePlayer player) {
        player.getDisguise().disguiseAs(DefaultFactions.NEUTRAL);
    }

    @Override
    public int getCooldown(IVampirePlayer player) {
        return ModConfig.balance().vaDisguiseCooldown.get() * 20;
    }

    @Override
    public int getDuration(IVampirePlayer player) {
        return ModConfig.balance().vaDisguiseDuration.get() * 20;
    }

    @Override
    public boolean isEnabled() {
        return ModConfig.balance().vaDisguiseEnabled.get();
    }

    @Override
    public void onActivatedClient(IVampirePlayer player) {
        activate(player);
    }

    @Override
    public void onDeactivated(IVampirePlayer player) {
        player.getDisguise().unDisguise();
    }

    @Override
    public void onReActivated(IVampirePlayer player) {
        activate(player);
    }

    @Override
    public boolean onUpdate(de.teamlapen.vampirism.api.world.entity.player.vampire.IVampirePlayer player) {
        return ILastingAction.super.onUpdate(player);
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

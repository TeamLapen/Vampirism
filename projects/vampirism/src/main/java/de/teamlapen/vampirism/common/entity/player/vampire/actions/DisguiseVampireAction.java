package de.teamlapen.vampirism.common.entity.player.vampire.actions;

import de.teamlapen.factions.common.core.DefaultFactions;
import de.teamlapen.factions.api.actions.IActionResult;
import de.teamlapen.factions.api.actions.ILastingAction;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.common.config.ModConfig;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Disguise skill
 */
public class DisguiseVampireAction extends DefaultVampireAction implements ILastingAction<IVampirePlayer> {

    public DisguiseVampireAction() {
        super();
    }

    @Override
    public IActionResult activate(@NotNull IVampirePlayer player, ActivationContext context) {
        activate(player);
        return IActionResult.SUCCESS;
    }

    protected void activate(@NotNull IVampirePlayer player) {
        player.getDisguise().disguiseAs(DefaultFactions.NEUTRAL);
    }

    @Override
    public int getCooldown(IVampirePlayer player) {
        return ModConfig.BALANCE.vaDisguiseCooldown.get() * 20;
    }

    @Override
    public int getDuration(IVampirePlayer player) {
        return ModConfig.BALANCE.vaDisguiseDuration.get() * 20;
    }

    @Override
    public boolean isEnabled() {
        return ModConfig.BALANCE.vaDisguiseEnabled.get();
    }

    @Override
    public void onActivatedClient(@NotNull IVampirePlayer player) {
        activate(player);
    }

    @Override
    public void onDeactivated(@NotNull IVampirePlayer player) {
        player.getDisguise().unDisguise();
    }

    @Override
    public void onReActivated(@NotNull IVampirePlayer player) {
        activate(player);
    }

    @Override
    public boolean onUpdate(IVampirePlayer player) {
        return false;
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

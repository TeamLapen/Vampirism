package de.teamlapen.vampirism.entity.player.vampire.actions;

import de.teamlapen.vampirism.api.entity.player.actions.ILastingAction;
import de.teamlapen.vampirism.api.entity.player.vampire.DefaultVampireAction;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.entity.player.vampire.VampirePlayer;
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
    public boolean activate(@NotNull IVampirePlayer player, ActivationContext context) {
        activate(player);
        return true;
    }

    protected void activate(@NotNull IVampirePlayer player) {
        player.getDisguise().disguiseAs(null);
    }

    @Override
    public int getCooldown(IVampirePlayer player) {
        return VampirismConfig.BALANCE.vaDisguiseCooldown.get() * 20;
    }

    @Override
    public int getDuration(IVampirePlayer player) {
        return VampirismConfig.BALANCE.vaDisguiseDuration.get() * 20;
    }

    @Override
    public boolean isEnabled() {
        return VampirismConfig.BALANCE.vaDisguiseEnabled.get();
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

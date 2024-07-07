package de.teamlapen.vampirism.entity.player.vampire.actions;

import de.teamlapen.vampirism.api.entity.player.actions.IActionResult;
import de.teamlapen.vampirism.api.entity.player.actions.ILastingAction;
import de.teamlapen.vampirism.api.entity.player.vampire.DefaultVampireAction;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.entity.player.vampire.VampirePlayer;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;


public class InvisibilityVampireAction extends DefaultVampireAction implements ILastingAction<IVampirePlayer> {
    public InvisibilityVampireAction() {
        super();
    }

    @Override
    public IActionResult activate(@NotNull IVampirePlayer vampire, ActivationContext context) {
        activate(vampire);
        return IActionResult.SUCCESS;
    }

    protected void activate(@NotNull IVampirePlayer player) {
        player.asEntity().setInvisible(true);
    }

    @Override
    public int getCooldown(IVampirePlayer player) {
        return VampirismConfig.BALANCE.vaInvisibilityCooldown.get() * 20;
    }

    @Override
    public int getDuration(IVampirePlayer player) {
        return VampirismConfig.BALANCE.vaInvisibilityDuration.get() * 20;
    }

    @Override
    public boolean isEnabled() {
        return VampirismConfig.BALANCE.vaInvisibilityEnabled.get();
    }

    @Override
    public void onActivatedClient(@NotNull IVampirePlayer vampire) {
        ((VampirePlayer) vampire).getSpecialAttributes().invisible = true;
    }

    @Override
    public void onDeactivated(@NotNull IVampirePlayer vampire) {
        vampire.asEntity().setInvisible(false);
        ((VampirePlayer) vampire).getSpecialAttributes().invisible = false;
    }

    @Override
    public void onReActivated(@NotNull IVampirePlayer vampire) {
        activate(vampire);
    }

    @Override
    public boolean onUpdate(@NotNull IVampirePlayer vampire) {
        if (!vampire.asEntity().isInvisible()) {
            vampire.asEntity().setInvisible(true);
        }
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

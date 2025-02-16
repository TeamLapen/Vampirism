package de.teamlapen.vampirism.entity.player.vampire.actions;

import de.teamlapen.vampirism.api.entity.player.actions.ILastingAction;
import de.teamlapen.vampirism.api.entity.player.vampire.DefaultVampireAction;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.entity.player.vampire.VampirePlayer;
import net.minecraft.util.Mth;

public class JumpBoostAction extends DefaultVampireAction implements ILastingAction<IVampirePlayer> {
    
    @Override
    public boolean isEnabled() {
        return VampirismConfig.BALANCE.vaJumpBoostEnabled.get();
    }

    @Override
    protected boolean activate(IVampirePlayer player, ActivationContext context) {
        applyEffect(player);
        return true;
    }

    @Override
    public int getDuration(IVampirePlayer player) {
        return Mth.clamp(VampirismConfig.BALANCE.vaJumpBoostDuration.get(), 10, Integer.MAX_VALUE / 20 - 1) * 20;
    }
    private void applyEffect(IVampirePlayer vampire) {
        ((VampirePlayer) vampire).getSpecialAttributes().setJumpBoost(VampirismConfig.BALANCE.vsJumpBoost.get() + 1);
    }

    @Override
    public void onActivatedClient(IVampirePlayer player) {
        applyEffect(player);
    }

    @Override
    public void onDeactivated(IVampirePlayer player) {
        ((VampirePlayer) player).getSpecialAttributes().setJumpBoost(0);
    }

    @Override
    public void onReActivated(IVampirePlayer player) {
        applyEffect(player);
    }

    @Override
    public boolean onUpdate(IVampirePlayer player) {
        return false;
    }

    @Override
    public int getCooldown(IVampirePlayer player) {
        return VampirismConfig.BALANCE.vaJumpBoostCooldown.get() * 20 + 1;
    }
}

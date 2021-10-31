package de.teamlapen.vampirism.player.vampire.actions;

import de.teamlapen.vampirism.api.entity.player.actions.ILastingAction;
import de.teamlapen.vampirism.api.entity.player.vampire.DefaultVampireAction;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.player.vampire.VampirePlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;

public class HalfInvulnerableAction extends DefaultVampireAction implements ILastingAction<IVampirePlayer> {

    public HalfInvulnerableAction() {
        super();
    }

    @Override
    public int getCooldown(IVampirePlayer player) {
        return 20 * (VampirismConfig.BALANCE.vaHalfInvulnerableCooldown.get());
    }

    @Override
    public int getDuration(IVampirePlayer player) {
        return 20 * (VampirismConfig.BALANCE.vaHalfInvulnerableDuration.get());
    }

    @Override
    public boolean isEnabled() {
        return VampirismConfig.BALANCE.vaHalfInvulnerableEnabled.get();
    }

    @Override
    public void onActivatedClient(IVampirePlayer player) {
    }

    @Override
    public void onDeactivated(IVampirePlayer player) {
        ((VampirePlayer) player).getSpecialAttributes().half_invulnerable = false;
        removePotionEffect(player, MobEffects.MOVEMENT_SLOWDOWN);
    }

    @Override
    public void onReActivated(IVampirePlayer player) {
        ((VampirePlayer) player).getSpecialAttributes().half_invulnerable = true;

    }

    @Override
    public boolean onUpdate(IVampirePlayer player) {
        return false;
    }

    @Override
    protected boolean activate(IVampirePlayer vampire) {
        ((VampirePlayer) vampire).getSpecialAttributes().half_invulnerable = true;
        addEffectInstance(vampire, new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, getDuration(vampire) - 1, 1, false, false));
        return true;
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

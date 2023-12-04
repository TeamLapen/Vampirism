package de.teamlapen.vampirism.entity.player.vampire.actions;

import de.teamlapen.vampirism.api.entity.player.actions.ILastingAction;
import de.teamlapen.vampirism.api.entity.player.vampire.DefaultVampireAction;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.entity.player.vampire.VampirePlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

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
    public void onDeactivated(@NotNull IVampirePlayer player) {
        ((VampirePlayer) player).getSpecialAttributes().half_invulnerable = false;
        removePotionEffect(player, MobEffects.MOVEMENT_SLOWDOWN);
    }

    @Override
    public void onReActivated(@NotNull IVampirePlayer player) {
        ((VampirePlayer) player).getSpecialAttributes().half_invulnerable = true;

    }

    @Override
    public boolean onUpdate(IVampirePlayer player) {
        return false;
    }

    @Override
    protected boolean activate(@NotNull IVampirePlayer vampire, ActivationContext context) {
        ((VampirePlayer) vampire).getSpecialAttributes().half_invulnerable = true;
        addEffectInstance(vampire, new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, vampire.getActionHandler().getModifiedDuration(this) - 1, 1, false, false));
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

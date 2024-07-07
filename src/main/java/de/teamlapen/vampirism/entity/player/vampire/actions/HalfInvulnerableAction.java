package de.teamlapen.vampirism.entity.player.vampire.actions;

import de.teamlapen.vampirism.api.entity.player.actions.IActionResult;
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
    public boolean onUpdate(IVampirePlayer vampire) {
        if (!vampire.isRemote() && vampire.asEntity().tickCount % 20 == 0) {
            applyEffect(vampire);
        }
        return false;
    }

    @Override
    protected IActionResult activate(@NotNull IVampirePlayer vampire, ActivationContext context) {
        ((VampirePlayer) vampire).getSpecialAttributes().half_invulnerable = true;
        applyEffect(vampire);
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

    protected void applyEffect(IVampirePlayer vampire) {
        addEffectInstance(vampire, new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 22, 1, false, false));
    }

}

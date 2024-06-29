package de.teamlapen.vampirism.entity.player.vampire.actions;

import de.teamlapen.vampirism.api.entity.player.actions.ILastingAction;
import de.teamlapen.vampirism.api.entity.player.vampire.DefaultVampireAction;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModEffects;
import de.teamlapen.vampirism.core.ModRefinements;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public class SunscreenVampireAction extends DefaultVampireAction implements ILastingAction<IVampirePlayer> {

    @Override
    public boolean activate(@NotNull IVampirePlayer vampire, ActivationContext context) {
        applyEffect(vampire);
        return true;
    }

    @Override
    public int getCooldown(IVampirePlayer player) {
        return VampirismConfig.BALANCE.vaSunscreenCooldown.get() * 20;
    }


    @Override
    public int getDuration(@NotNull IVampirePlayer player) {
        int duration = 20 * (VampirismConfig.BALANCE.vaSunscreenDuration.get());
        if (player.getRefinementHandler().isRefinementEquipped(ModRefinements.SUN_SCREEN)) {
            duration *= VampirismConfig.BALANCE.vrSunscreenDurationMod.get();
        }
        return duration;
    }

    @Override
    public boolean isEnabled() {
        return VampirismConfig.BALANCE.vaSunscreenEnabled.get();
    }

    @Override
    public void onActivatedClient(IVampirePlayer vampire) {

    }

    @Override
    public void onDeactivated(@NotNull IVampirePlayer vampire) {
        removePotionEffect(vampire, ModEffects.SUNSCREEN);
    }

    @Override
    public void onReActivated(IVampirePlayer vampire) {

    }

    @Override
    public boolean onUpdate(IVampirePlayer vampire) {
        if (!vampire.isRemote() && vampire.asEntity().tickCount % 20 == 0) {
            applyEffect(vampire);
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

    protected void applyEffect(IVampirePlayer vampire) {
        addEffectInstance(vampire, new MobEffectInstance(ModEffects.SUNSCREEN, 22, 3, false, false));
    }
}

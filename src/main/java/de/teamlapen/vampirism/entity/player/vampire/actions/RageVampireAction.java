package de.teamlapen.vampirism.entity.player.vampire.actions;

import de.teamlapen.vampirism.api.entity.player.actions.IActionResult;
import de.teamlapen.vampirism.api.entity.player.actions.ILastingAction;
import de.teamlapen.vampirism.api.entity.player.vampire.DefaultVampireAction;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.config.VampirismConfig;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public class RageVampireAction extends DefaultVampireAction implements ILastingAction<IVampirePlayer> {

    public RageVampireAction() {
        super();
    }

    @Override
    public IActionResult activate(@NotNull IVampirePlayer vampire, ActivationContext context) {
        applyEffects(vampire);
        return IActionResult.SUCCESS;
    }

    @Override
    public IActionResult canBeUsedBy(@NotNull IVampirePlayer vampire) {
        return IActionResult.otherAction(vampire.getActionHandler(), VampireActions.BAT);
    }

    @Override
    public int getCooldown(IVampirePlayer player) {
        return VampirismConfig.BALANCE.vaRageCooldown.get() * 20;
    }

    @Override
    public int getDuration(@NotNull IVampirePlayer player) {
        return 20 * (VampirismConfig.BALANCE.vaRageMinDuration.get() + VampirismConfig.BALANCE.vaRageDurationIncrease.get() * player.getLevel());
    }

    @Override
    public boolean isEnabled() {
        return VampirismConfig.BALANCE.vaRageEnabled.get();
    }

    @Override
    public void onActivatedClient(IVampirePlayer vampire) {

    }

    @Override
    public void onDeactivated(@NotNull IVampirePlayer vampire) {
        removePotionEffect(vampire, MobEffects.MOVEMENT_SPEED);
        removePotionEffect(vampire, MobEffects.DAMAGE_BOOST);
        removePotionEffect(vampire, MobEffects.DIG_SPEED);
    }

    @Override
    public void onReActivated(IVampirePlayer vampire) {

    }

    @Override
    public boolean onUpdate(IVampirePlayer vampire) {
        if (!vampire.isRemote() && vampire.asEntity().tickCount % 20 == 0) {
            applyEffects(vampire);
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

    protected void applyEffects(IVampirePlayer vampire) {
        addEffectInstance(vampire, new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 22, 2, false, false));
        addEffectInstance(vampire, new MobEffectInstance(MobEffects.DAMAGE_BOOST, 22, 0, false, false));
        addEffectInstance(vampire, new MobEffectInstance(MobEffects.DIG_SPEED, 22, 0, false, false));
    }
}

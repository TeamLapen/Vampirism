package de.teamlapen.vampirism.common.world.entity.player.vampire.actions;

import de.teamlapen.faction.api.factions.actions.IActionResult;
import de.teamlapen.faction.api.factions.actions.ILastingAction;
import de.teamlapen.vampirism.api.world.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.common.config.ModConfig;
import de.teamlapen.vampirism.common.core.ModEffects;
import de.teamlapen.vampirism.common.core.ModRefinements;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public class SunscreenVampireAction extends DefaultVampireAction implements ILastingAction<IVampirePlayer> {

    @Override
    public IActionResult activateServer(@NotNull IVampirePlayer vampire, ActivationContext context) {
        applyEffect(vampire);
        return IActionResult.SUCCESS;
    }

    @Override
    public int getCooldown(IVampirePlayer player) {
        return ModConfig.balance().vaSunscreenCooldown.get() * 20;
    }


    @Override
    public int getDuration(@NotNull IVampirePlayer player) {
        int duration = 20 * (ModConfig.balance().vaSunscreenDuration.get());
        if (player.getRefinementHandler().isRefinementEquipped(ModRefinements.SUN_SCREEN)) {
            duration *= ModConfig.balance().vrSunscreenDurationMod.get();
        }
        return duration;
    }

    @Override
    public boolean isEnabled() {
        return ModConfig.balance().vaSunscreenEnabled.get();
    }

    @Override
    public void onActivatedClient(IVampirePlayer vampire) {

    }

    @Override
    public void onDeactivated(@NotNull IVampirePlayer vampire) {
        removePotionEffect(vampire, ModEffects.SUNSCREEN);
    }

    @Override
    public void onReActivatedServer(IVampirePlayer vampire) {

    }

    @Override
    public boolean onUpdate(IVampirePlayer vampire) {
        if (!vampire.isRemote() && vampire.asEntity().tickCount % 20 == 0) {
            applyEffect(vampire);
        }

        return false;
    }

    protected void applyEffect(IVampirePlayer vampire) {
        addEffectInstance(vampire, new MobEffectInstance(ModEffects.SUNSCREEN, 22, 3, false, false));
    }
}

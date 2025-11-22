package de.teamlapen.vampirism.common.entity.player.vampire.actions;

import de.teamlapen.factions.api.actions.IActionResult;
import de.teamlapen.factions.api.actions.ILastingAction;
import de.teamlapen.vampirism.common.entity.player.vampire.DefaultVampireAction;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.common.config.ModConfig;
import de.teamlapen.vampirism.common.core.ModRefinements;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;


public class RegenVampireAction extends DefaultVampireAction implements ILastingAction<IVampirePlayer> {

    @Override
    public IActionResult activate(@NotNull IVampirePlayer vampire, ActivationContext context) {
        applyEffect(vampire);
        return IActionResult.SUCCESS;
    }

    @Override
    public int getCooldown(IVampirePlayer player) {
        return ModConfig.BALANCE.vaRegenerationCooldown.get() * 20;
    }

    @Override
    public boolean isEnabled() {
        return ModConfig.BALANCE.vaRegenerationEnabled.get();
    }

    @Override
    public boolean showHudCooldown(Player player) {
        return true;
    }

    @Override
    public boolean showHudDuration(Player player) {
        return true;
    }


    @Override
    public int getDuration(IVampirePlayer player) {
        return ModConfig.BALANCE.vaRegenerationDuration.get() * 20;
    }

    @Override
    public void onActivatedClient(IVampirePlayer player) {

    }

    @Override
    public void onDeactivated(IVampirePlayer player) {
        removePotionEffect(player, MobEffects.REGENERATION);
    }

    @Override
    public void onReActivated(IVampirePlayer player) {

    }

    @Override
    public boolean onUpdate(IVampirePlayer vampire) {
        if (!vampire.isRemote() && vampire.asEntity().tickCount % 50 == 0) {
            applyEffect(vampire);
        }
        return false;
    }

    protected void applyEffect(IVampirePlayer vampire) {
        addEffectInstance(vampire, new MobEffectInstance(MobEffects.REGENERATION, 52, vampire.getRefinementHandler().isRefinementEquipped(ModRefinements.REGENERATION) ? 1 : 0, false, false));
    }
}

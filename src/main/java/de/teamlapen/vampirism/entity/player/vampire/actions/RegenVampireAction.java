package de.teamlapen.vampirism.entity.player.vampire.actions;

import de.teamlapen.vampirism.api.entity.player.actions.ILastingAction;
import de.teamlapen.vampirism.api.entity.player.vampire.DefaultVampireAction;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModRefinements;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;


public class RegenVampireAction extends DefaultVampireAction implements ILastingAction<IVampirePlayer> {

    @Override
    public boolean activate(@NotNull IVampirePlayer vampire, ActivationContext context) {
        applyEffect(vampire);
        return true;
    }

    @Override
    public int getCooldown(IVampirePlayer player) {
        return VampirismConfig.BALANCE.vaRegenerationCooldown.get() * 20;
    }

    @Override
    public boolean isEnabled() {
        return VampirismConfig.BALANCE.vaRegenerationEnabled.get();
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
        return VampirismConfig.BALANCE.vaRegenerationDuration.get() * 20;
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
        addEffectInstance(vampire, new MobEffectInstance(MobEffects.REGENERATION, 52, vampire.getSkillHandler().isRefinementEquipped(ModRefinements.REGENERATION.get()) ? 1 : 0, false, false));
    }
}

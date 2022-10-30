package de.teamlapen.vampirism.entity.player.vampire.actions;

import de.teamlapen.vampirism.api.entity.player.vampire.DefaultVampireAction;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModRefinements;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;


public class RegenVampireAction extends DefaultVampireAction {

    public RegenVampireAction() {
        super();
    }

    @Override
    public boolean activate(@NotNull IVampirePlayer vampire, ActivationContext context) {
        Player player = vampire.getRepresentingPlayer();
        int dur = VampirismConfig.BALANCE.vaRegenerationDuration.get() * 20;
        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, dur, vampire.getSkillHandler().isRefinementEquipped(ModRefinements.REGENERATION.get()) ? 1 : 0));
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
}

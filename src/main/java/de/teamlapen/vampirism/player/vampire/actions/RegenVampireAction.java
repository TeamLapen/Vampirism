package de.teamlapen.vampirism.player.vampire.actions;

import de.teamlapen.vampirism.api.entity.player.vampire.DefaultVampireAction;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModEffects;
import de.teamlapen.vampirism.core.ModRefinements;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;


public class RegenVampireAction extends DefaultVampireAction {

    public RegenVampireAction() {
        super();
    }

    @Override
    public boolean activate(IVampirePlayer vampire) {
        PlayerEntity player = vampire.getRepresentingPlayer();
        int dur = VampirismConfig.BALANCE.vaRegenerationDuration.get() * 20;
        player.addEffect(new EffectInstance(Effects.REGENERATION, dur, vampire.getSkillHandler().isRefinementEquipped(ModRefinements.REGENERATION.get()) ? 1 : 0));
        player.addEffect(new EffectInstance(ModEffects.THIRST.get(), dur, 2));
        return true;
    }

    @Override
    public int getCooldown() {
        return VampirismConfig.BALANCE.vaRegenerationCooldown.get() * 20;
    }

    @Override
    public boolean isEnabled() {
        return VampirismConfig.BALANCE.vaRegenerationEnabled.get();
    }

    @Override
    public boolean showHudCooldown(PlayerEntity player) {
        return true;
    }
}

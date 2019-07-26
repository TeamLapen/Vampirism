package de.teamlapen.vampirism.player.vampire.actions;

import de.teamlapen.vampirism.api.entity.player.vampire.DefaultVampireAction;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.config.Balance;
import de.teamlapen.vampirism.core.ModEffects;
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
        int dur = Balance.vpa.REGEN_DURATION * 20;
        player.addPotionEffect(new EffectInstance(Effects.REGENERATION, dur, 0));
        player.addPotionEffect(new EffectInstance(ModEffects.thirst, dur, 2));
        return true;
    }

    @Override
    public int getCooldown() {
        return Balance.vpa.REGEN_COOLDOWN * 20;
    }

    @Override
    public boolean isEnabled() {
        return Balance.vpa.REGEN_ENABLED;
    }
}

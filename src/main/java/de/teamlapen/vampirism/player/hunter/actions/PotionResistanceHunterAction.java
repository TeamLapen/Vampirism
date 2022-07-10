package de.teamlapen.vampirism.player.hunter.actions;

import de.teamlapen.vampirism.api.entity.player.actions.ILastingAction;
import de.teamlapen.vampirism.api.entity.player.hunter.DefaultHunterAction;
import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;

import java.util.Collection;
import java.util.stream.Collectors;


public class PotionResistanceHunterAction extends DefaultHunterAction implements ILastingAction<IHunterPlayer> {

    public static boolean shouldRemove(Effect effect) {
        return effect == Effects.BLINDNESS || effect == Effects.CONFUSION || effect == Effects.HUNGER || effect == Effects.POISON || effect == ModEffects.FREEZE.get();
    }

    @Override
    public int getCooldown() {
        return VampirismConfig.BALANCE.haPotionResistanceCooldown.get();
    }

    @Override
    public int getDuration(int level) {
        return VampirismConfig.BALANCE.haPotionResistanceDuration.get();
    }

    @Override
    public boolean isEnabled() {
        return VampirismConfig.BALANCE.haPotionResistanceEnabled.get();
    }

    @Override
    public void onActivatedClient(IHunterPlayer player) {
        onUpdate(player);
    }

    @Override
    public void onDeactivated(IHunterPlayer player) {
    }

    @Override
    public void onReActivated(IHunterPlayer player) {
        onUpdate(player);
    }

    @Override
    public boolean onUpdate(IHunterPlayer player) {
        if (!(player.getRepresentingEntity().tickCount % 3 == 0)) {
            Collection<EffectInstance> effects = player.getRepresentingEntity().getActiveEffects();
            effects.stream().filter(instance -> shouldRemove(instance.getEffect())).collect(Collectors.toList()).forEach(s -> player.getRepresentingPlayer().removeEffect(s.getEffect()));
        }
        return false;
    }

    @Override
    protected boolean activate(IHunterPlayer player) {
        onUpdate(player);
        return true;
    }

    @Override
    public boolean showHudCooldown(PlayerEntity player) {
        return true;
    }

    @Override
    public boolean showHudDuration(PlayerEntity player) {
        return true;
    }
}

package de.teamlapen.vampirism.player.hunter.actions;

import de.teamlapen.vampirism.api.entity.player.actions.ILastingAction;
import de.teamlapen.vampirism.api.entity.player.hunter.DefaultHunterAction;
import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModEffects;
import de.teamlapen.vampirism.player.hunter.HunterPlayer;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;

import java.util.Collection;


public class PotionResistanceHunterAction extends DefaultHunterAction implements ILastingAction<IHunterPlayer> {

    public static boolean shouldRemove(Effect effect) {
        return effect == Effects.BLINDNESS || effect == Effects.NAUSEA || effect == Effects.HUNGER || effect == Effects.POISON || effect == ModEffects.freeze;
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
        ((HunterPlayer) player).getSpecialAttributes().resetVampireNearby();
    }

    @Override
    public void onReActivated(IHunterPlayer player) {
        onUpdate(player);
    }

    @Override
    public boolean onUpdate(IHunterPlayer player) {
        if (!(player.getRepresentingEntity().ticksExisted % 3 == 0)) {
            Collection<EffectInstance> effects = player.getRepresentingEntity().getActivePotionEffects();
            for (EffectInstance instance : effects) {
                if (shouldRemove(instance.getPotion())) {
                    player.getRepresentingPlayer().removePotionEffect(instance.getPotion());
                }
            }
        }
        return false;
    }

    @Override
    protected boolean activate(IHunterPlayer player) {
        onUpdate(player);
        return true;
    }
}

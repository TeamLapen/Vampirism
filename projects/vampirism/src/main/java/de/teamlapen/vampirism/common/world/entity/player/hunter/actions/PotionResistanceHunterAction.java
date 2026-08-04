package de.teamlapen.vampirism.common.world.entity.player.hunter.actions;

import de.teamlapen.faction.api.factions.actions.IActionResult;
import de.teamlapen.faction.api.factions.actions.ILastingAction;
import de.teamlapen.vampirism.api.world.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.common.config.ModConfig;
import de.teamlapen.vampirism.common.tags.ModEffectTags;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;

import java.util.Collection;


public class PotionResistanceHunterAction extends DefaultHunterAction implements ILastingAction<IHunterPlayer> {

    public static boolean shouldRemove(Holder<MobEffect> effect) {
        return effect.is(ModEffectTags.HUNTER_POTION_RESISTANCE);
    }

    @Override
    public int getCooldown(IHunterPlayer player) {
        return ModConfig.balance().haPotionResistanceCooldown.get();
    }

    @Override
    public int getDuration(IHunterPlayer player) {
        return ModConfig.balance().haPotionResistanceDuration.get();
    }

    @Override
    public boolean isEnabled() {
        return ModConfig.balance().haPotionResistanceEnabled.get();
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
        if (!(player.asEntity().tickCount % 3 == 0)) {
            Collection<MobEffectInstance> effects = player.asEntity().getActiveEffects();
            effects.stream().filter(instance -> shouldRemove(instance.getEffect())).toList().forEach(s -> player.asEntity().removeEffect(s.getEffect()));
        }
        return false;
    }

    @Override
    protected IActionResult activate(IHunterPlayer player, ActivationContext context) {
        onUpdate(player);
        return IActionResult.SUCCESS;
    }

}

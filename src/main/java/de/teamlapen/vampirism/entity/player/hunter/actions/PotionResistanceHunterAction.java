package de.teamlapen.vampirism.entity.player.hunter.actions;

import de.teamlapen.vampirism.api.entity.player.actions.ILastingAction;
import de.teamlapen.vampirism.api.entity.player.hunter.DefaultHunterAction;
import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModEffects;
import de.teamlapen.vampirism.core.ModTags;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;


public class PotionResistanceHunterAction extends DefaultHunterAction implements ILastingAction<IHunterPlayer> {

    public static boolean shouldRemove(Holder<MobEffect> effect) {
        return effect.is(ModTags.Effects.HUNTER_POTION_RESISTANCE);
    }

    @Override
    public int getCooldown(IHunterPlayer player) {
        return VampirismConfig.BALANCE.haPotionResistanceCooldown.get();
    }

    @Override
    public int getDuration(IHunterPlayer player) {
        return VampirismConfig.BALANCE.haPotionResistanceDuration.get();
    }

    @Override
    public boolean isEnabled() {
        return VampirismConfig.BALANCE.haPotionResistanceEnabled.get();
    }

    @Override
    public void onActivatedClient(@NotNull IHunterPlayer player) {
        onUpdate(player);
    }

    @Override
    public void onDeactivated(IHunterPlayer player) {
    }

    @Override
    public void onReActivated(@NotNull IHunterPlayer player) {
        onUpdate(player);
    }

    @Override
    public boolean onUpdate(@NotNull IHunterPlayer player) {
        if (!(player.asEntity().tickCount % 3 == 0)) {
            Collection<MobEffectInstance> effects = player.asEntity().getActiveEffects();
            effects.stream().filter(instance -> shouldRemove(instance.getEffect())).toList().forEach(s -> player.asEntity().removeEffect(s.getEffect()));
        }
        return false;
    }

    @Override
    protected boolean activate(@NotNull IHunterPlayer player, ActivationContext context) {
        onUpdate(player);
        return true;
    }

    @Override
    public boolean showHudCooldown(Player player) {
        return true;
    }

    @Override
    public boolean showHudDuration(Player player) {
        return true;
    }
}

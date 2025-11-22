package de.teamlapen.vampirism.common.entity.player.hunter.actions;

import de.teamlapen.factions.api.actions.IActionResult;
import de.teamlapen.factions.api.actions.ILastingAction;
import de.teamlapen.vampirism.common.entity.player.hunter.DefaultHunterAction;
import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.common.config.ModConfig;
import de.teamlapen.vampirism.common.tags.ModEffectTags;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;


public class PotionResistanceHunterAction extends DefaultHunterAction implements ILastingAction<IHunterPlayer> {

    public static boolean shouldRemove(Holder<MobEffect> effect) {
        return effect.is(ModEffectTags.HUNTER_POTION_RESISTANCE);
    }

    @Override
    public int getCooldown(@NotNull IHunterPlayer player) {
        return ModConfig.BALANCE.haPotionResistanceCooldown.get();
    }

    @Override
    public int getDuration(@NotNull IHunterPlayer player) {
        return ModConfig.BALANCE.haPotionResistanceDuration.get();
    }

    @Override
    public boolean isEnabled() {
        return ModConfig.BALANCE.haPotionResistanceEnabled.get();
    }

    @Override
    public void onActivatedClient(@NotNull IHunterPlayer player) {
        onUpdate(player);
    }

    @Override
    public void onDeactivated(@NotNull IHunterPlayer player) {
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
    protected @NotNull IActionResult activate(@NotNull IHunterPlayer player, @NotNull ActivationContext context) {
        onUpdate(player);
        return IActionResult.SUCCESS;
    }

    @Override
    public boolean showHudCooldown(@NotNull Player player) {
        return true;
    }

    @Override
    public boolean showHudDuration(@NotNull Player player) {
        return true;
    }
}

package de.teamlapen.vampirism.entity.player.vampire;

import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampireVision;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.effects.VampireNightVisionEffectInstance;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import org.jetbrains.annotations.NotNull;

/**
 * Night Vision
 */
public class NightVision implements IVampireVision {


    @Override
    public @NotNull String getTranslationKey() {
        return "text.vampirism.skill.night_vision";
    }

    @Override
    public void onActivated(@NotNull IVampirePlayer player) {
        if (player.isRemote()) {
            player.getRepresentingPlayer().addEffect(new VampireNightVisionEffectInstance());
        }
    }

    @Override
    public void onDeactivated(@NotNull IVampirePlayer player) {
        MobEffectInstance nightVision = player.getRepresentingPlayer().getEffect(MobEffects.NIGHT_VISION);
        if (nightVision instanceof VampireNightVisionEffectInstance) {
            player.getRepresentingPlayer().removeEffect(nightVision.getEffect());
        }
    }

    @Override
    public void tick(@NotNull IVampirePlayer player) {
        if (player.getRepresentingPlayer().tickCount % 50 == 8) {
            MobEffectInstance effect = player.getRepresentingPlayer().getEffect(MobEffects.NIGHT_VISION);
            if (!(effect instanceof VampireNightVisionEffectInstance)) {
                player.getRepresentingPlayer().removeEffectNoUpdate(MobEffects.NIGHT_VISION);
                effect = null;
            }
            if (effect == null) {
                player.getRepresentingPlayer().addEffect(new VampireNightVisionEffectInstance());

            }
        }
    }

    @Override
    public boolean isEnabled() {
        return !VampirismConfig.BALANCE.vpNightVisionDisabled.get();
    }
}

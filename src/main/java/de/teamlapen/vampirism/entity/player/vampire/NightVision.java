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
            player.asEntity().addEffect(new VampireNightVisionEffectInstance());
        }
    }

    @Override
    public void onDeactivated(@NotNull IVampirePlayer player) {
        MobEffectInstance nightVision = player.asEntity().getEffect(MobEffects.NIGHT_VISION);
        if (nightVision instanceof VampireNightVisionEffectInstance) {
            player.asEntity().removeEffect(nightVision.getEffect());
        }
    }

    @Override
    public void tick(@NotNull IVampirePlayer player) {
        if (player.asEntity().tickCount % 50 == 8) {
            MobEffectInstance effect = player.asEntity().getEffect(MobEffects.NIGHT_VISION);
            if (!(effect instanceof VampireNightVisionEffectInstance)) {
                player.asEntity().removeEffectNoUpdate(MobEffects.NIGHT_VISION);
                effect = null;
            }
            if (effect == null) {
                player.asEntity().addEffect(new VampireNightVisionEffectInstance());

            }
        }
    }

    @Override
    public boolean isEnabled() {
        return !VampirismConfig.BALANCE.vpNightVisionDisabled.get();
    }
}

package de.teamlapen.vampirism.common.world.entity.player.vampire.vision;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.world.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.api.world.entity.player.vampire.IVampireVision;
import de.teamlapen.vampirism.common.config.ModConfig;
import de.teamlapen.vampirism.common.world.effects.ModEffectInstanceHelper;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

/**
 * Night Vision
 */
public class NightVision implements IVampireVision {


    @Override
    public String getTranslationKey() {
        return "text.vampirism.skill.night_vision";
    }

    @Override
    public void onActivated(IVampirePlayer player) {
        if (player.isRemote()) {
            player.asEntity().addEffect(ModEffectInstanceHelper.createNightVision());
        }
    }

    @Override
    public void onDeactivated(IVampirePlayer player) {
        MobEffectInstance nightVision = player.asEntity().getEffect(MobEffects.NIGHT_VISION);
        if (ModEffectInstanceHelper.hasSource(nightVision, VReference.VAMPIRE_NIGHT_VISION_EFFECT)) {
            player.asEntity().removeEffect(nightVision.getEffect());
        }
    }

    @Override
    public void tick(IVampirePlayer player) {
        if (player.asEntity().tickCount % 50 == 8) {
            MobEffectInstance effect = player.asEntity().getEffect(MobEffects.NIGHT_VISION);
            if (!ModEffectInstanceHelper.hasSource(effect, VReference.VAMPIRE_NIGHT_VISION_EFFECT)) {
                player.asEntity().removeEffectNoUpdate(MobEffects.NIGHT_VISION);
                effect = null;
            }
            if (effect == null) {
                player.asEntity().addEffect(ModEffectInstanceHelper.createNightVision());

            }
        }
    }

    @Override
    public boolean isEnabled() {
        return !ModConfig.balance().vpNightVisionDisabled.get();
    }
}

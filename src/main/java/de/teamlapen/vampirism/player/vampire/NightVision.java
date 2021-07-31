package de.teamlapen.vampirism.player.vampire;

import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampireVision;
import de.teamlapen.vampirism.effects.VampireNightVisionEffectInstance;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;

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
            player.getRepresentingPlayer().addEffect(new VampireNightVisionEffectInstance());
        }
    }

    @Override
    public void onDeactivated(IVampirePlayer player) {
        EffectInstance nightVision = player.getRepresentingPlayer().getEffect(Effects.NIGHT_VISION);
        if (nightVision instanceof VampireNightVisionEffectInstance) {
            player.getRepresentingPlayer().removeEffect(nightVision.getEffect());
        }
    }

    @Override
    public void tick(IVampirePlayer player) {
        if (player.getRepresentingPlayer().tickCount % 50 == 8) {
            EffectInstance effect = player.getRepresentingPlayer().getEffect(Effects.NIGHT_VISION);
            if (!(effect instanceof VampireNightVisionEffectInstance)) {
                player.getRepresentingPlayer().removeEffectNoUpdate(Effects.NIGHT_VISION);
                effect = null;
            }
            if (effect == null) {
                player.getRepresentingPlayer().addEffect(new VampireNightVisionEffectInstance());

            }
        }
    }
}

package de.teamlapen.vampirism.player.vampire;

import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampireVision;
import de.teamlapen.vampirism.potion.VampireNightVisionEffect;
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
            player.getRepresentingPlayer().addPotionEffect(new VampireNightVisionEffect());
        }
    }

    @Override
    public void onDeactivated(IVampirePlayer player) {
        EffectInstance nightVision = player.getRepresentingPlayer().getActivePotionEffect(Effects.NIGHT_VISION);
        if (nightVision instanceof VampireNightVisionEffect) {
            player.getRepresentingPlayer().removePotionEffect(nightVision.getPotion());
        }
    }

    @Override
    public void tick(IVampirePlayer player) {
        if (player.getRepresentingPlayer().ticksExisted % 50 == 8) {
            EffectInstance effect = player.getRepresentingPlayer().getActivePotionEffect(Effects.NIGHT_VISION);
            if (!(effect instanceof VampireNightVisionEffect)) {
                player.getRepresentingPlayer().removeActivePotionEffect(Effects.NIGHT_VISION);
                effect = null;
            }
            if (effect == null) {
                player.getRepresentingPlayer().addPotionEffect(new VampireNightVisionEffect());

            }
        }
    }
}

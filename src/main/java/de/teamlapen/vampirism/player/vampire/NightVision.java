package de.teamlapen.vampirism.player.vampire;

import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampireVision;
import de.teamlapen.vampirism.potion.FakeNightVisionPotionEffect;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;

/**
 * Night Vision
 */
public class NightVision implements IVampireVision {


    @Override
    public String getUnlocName() {
        return "text.vampirism.skill.night_vision";
    }

    @Override
    public void onActivated(IVampirePlayer player) {
        if (player.isRemote()) {
            player.getRepresentingPlayer().addPotionEffect(new FakeNightVisionPotionEffect());
        }
    }

    @Override
    public void onDeactivated(IVampirePlayer player) {
        PotionEffect nightVision = player.getRepresentingPlayer().getActivePotionEffect(MobEffects.NIGHT_VISION);
        if (nightVision instanceof FakeNightVisionPotionEffect) {
            player.getRepresentingPlayer().removePotionEffect(nightVision.getPotion());
        }
    }

    @Override
    public void onUpdate(IVampirePlayer player) {
        if (player.getRepresentingPlayer().ticksExisted % 50 == 8) {
            PotionEffect effect = player.getRepresentingPlayer().getActivePotionEffect(MobEffects.NIGHT_VISION);
            if (!(effect instanceof FakeNightVisionPotionEffect)) {
                player.getRepresentingPlayer().removeActivePotionEffect(MobEffects.NIGHT_VISION);
                effect = null;
            }
            if (effect == null) {
                player.getRepresentingPlayer().addPotionEffect(new FakeNightVisionPotionEffect());

            }
        }
    }
}

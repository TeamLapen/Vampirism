package de.teamlapen.vampirism.client.core;

import de.teamlapen.vampirism.client.extensions.EffectExtensions;
import de.teamlapen.vampirism.mixin.client.accessor.MobEffectAccessor;
import net.minecraft.world.effect.MobEffects;

public class ModEffects {

    public static void modifyNightVisionRenderer() {
        MobEffectAccessor effect = (MobEffectAccessor) MobEffects.NIGHT_VISION.value();
        if (effect.getEffectRenderer() == null) {
            effect.setEffectRenderer(EffectExtensions.NIGHT_VISION);
        }
    }
}

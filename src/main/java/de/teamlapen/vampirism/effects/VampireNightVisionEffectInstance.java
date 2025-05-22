package de.teamlapen.vampirism.effects;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.effect.EffectInstanceWithSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

/**
 * Night vision effect for vampire players which is not displayed
 */
public class VampireNightVisionEffectInstance extends MobEffectInstance {

    public VampireNightVisionEffectInstance() {
        super(MobEffects.NIGHT_VISION, -1, 0, false, false, false);
        ((EffectInstanceWithSource) this).vampirism$setSource(VReference.PERMANENT_INVISIBLE_MOB_EFFECT);
    }

    @Override
    public boolean equals(Object other) {
        return other == this;
    }

    @Override
    public boolean tick(LivingEntity entity, Runnable onExpirationRunnable) {
        return true;
    }

    @Override
    public boolean update(MobEffectInstance other) {
        //Don't change anything
        return false;
    }
}

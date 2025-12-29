package de.teamlapen.vampirism.common.world.effects;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;

import java.util.function.Function;

/**
 * Base class for Vampirism's potions
 */
public class SimpleMobEffect extends MobEffect {

    public SimpleMobEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    public SimpleMobEffect(MobEffectCategory category, int color, ParticleOptions particle) {
        super(category, color, particle);
    }

    public SimpleMobEffect(MobEffectCategory category, int color, Function<MobEffectInstance, ParticleOptions> particleFactory) {
        super(category, color, particleFactory);
    }

}

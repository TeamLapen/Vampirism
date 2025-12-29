package de.teamlapen.factions.common.world.effects;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;

import java.util.function.Function;

public class ResurrectionFatigueMobEffect extends MobEffect {

    public ResurrectionFatigueMobEffect() {
        super(MobEffectCategory.HARMFUL, 0x8B0000);
    }
}

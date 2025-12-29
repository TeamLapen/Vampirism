package de.teamlapen.vampirism.common.world.effects;

import de.teamlapen.vampirism.common.core.ModEffects;
import de.teamlapen.vampirism.common.world.entity.player.vampire.VampirePlayer;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.function.Function;

/**
 * Base class for Vampirism's potions
 */
public class VampirismMobEffect extends MobEffect {

    public VampirismMobEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    public VampirismMobEffect(MobEffectCategory category, int color, ParticleOptions particle) {
        super(category, color, particle);
    }

    public VampirismMobEffect(MobEffectCategory category, int color, Function<MobEffectInstance, ParticleOptions> particleFactory) {
        super(category, color, particleFactory);
    }

    @Override
    public boolean applyEffectTick(ServerLevel level, LivingEntity entity, int amplifier) {
        if (this == ModEffects.ARMOR_REGENERATION.get()) {
            if (entity instanceof Player player && entity.isAlive()) {
                VampirePlayer.get(player).requestNaturalArmorUpdate();
            }
        }
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return this == ModEffects.ARMOR_REGENERATION.get() && duration % 100 == 1;
    }
}

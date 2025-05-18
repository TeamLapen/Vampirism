package de.teamlapen.vampirism.effects;

import de.teamlapen.vampirism.core.ModEffects;
import de.teamlapen.vampirism.entity.player.vampire.VampirePlayer;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.function.Function;

/**
 * Base class for Vampirism's potions
 */
public class VampirismEffect extends MobEffect {

    public VampirismEffect(@NotNull MobEffectCategory effectType, int potionColor) {
        super(effectType, potionColor);
    }
    public VampirismEffect(@NotNull MobEffectCategory effectType, int potionColor, ParticleOptions options) {
        super(effectType, potionColor, options);
    }
    public VampirismEffect(@NotNull MobEffectCategory effectType, int potionColor, Function<MobEffectInstance, ParticleOptions> options) {
        super(effectType, potionColor, options);
    }

    @Override
    public boolean applyEffectTick(@NotNull ServerLevel level, @NotNull LivingEntity entityLivingBaseIn, int amplifier) {
        if (this == ModEffects.ARMOR_REGENERATION.get()) {
            if (entityLivingBaseIn instanceof Player player && entityLivingBaseIn.isAlive()) {
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

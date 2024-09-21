package de.teamlapen.vampirism.effects;

import de.teamlapen.vampirism.core.ModEffects;
import de.teamlapen.vampirism.util.DamageHandler;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;


public class VampirismPoisonEffect extends VampirismEffect {

    public static final int DEADLY_AMPLIFIER = 4;

    public VampirismPoisonEffect(int potionColor) {
        super(MobEffectCategory.HARMFUL, potionColor);
    }

    @Override
    public boolean applyEffectTick(@NotNull LivingEntity entityLivingBaseIn, int amplifier) {
        float damage = amplifier >= DEADLY_AMPLIFIER ? amplifier : Math.min(entityLivingBaseIn.getHealth() - 1, Math.max(1,amplifier));
        if (damage > 0) {
            DamageHandler.hurtVanilla(entityLivingBaseIn, DamageSources::magic, damage);
        }
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        int j = 25 >> amplifier;
        if (j > 0) {
            return duration % j == 0;
        } else {
            return true;
        }
    }

    public static MobEffectInstance createThrowableEffect() {
        return new MobEffectInstance(ModEffects.POISON, 40, 1);
    }

    public static MobEffectInstance createEffectCloudEffect() {
        return new MobEffectInstance(ModEffects.POISON, 60, 1);
    }
}

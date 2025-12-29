package de.teamlapen.vampirism.common.world.effects;

import de.teamlapen.vampirism.common.core.ModEffects;
import de.teamlapen.vampirism.common.util.DamageHandler;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

public class VampirismPoisonMobEffect extends SimpleMobEffect {

    public static final int DEADLY_AMPLIFIER = 4;

    public VampirismPoisonMobEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    public static MobEffectInstance createThrowableEffect() {
        return new MobEffectInstance(ModEffects.POISON, 40, 1);
    }

    public static MobEffectInstance createEffectCloudEffect() {
        return new MobEffectInstance(ModEffects.POISON, 60, 1);
    }

    @Override
    public boolean applyEffectTick(ServerLevel level, LivingEntity entity, int amplifier) {
        float damage = amplifier >= DEADLY_AMPLIFIER ? amplifier : Math.min(entity.getHealth() - 1, Math.max(1, amplifier));
        if (damage > 0) {
            DamageHandler.hurtVanilla(level, entity, DamageSources::magic, damage);
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
}

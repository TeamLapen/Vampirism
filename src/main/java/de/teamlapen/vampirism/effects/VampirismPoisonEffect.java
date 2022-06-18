package de.teamlapen.vampirism.effects;

import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.EffectType;
import net.minecraft.util.DamageSource;


public class VampirismPoisonEffect extends VampirismEffect {

    public static int DEADLY_AMPLIFIER = 4;

    public VampirismPoisonEffect(int potionColor) {
        super(EffectType.HARMFUL, potionColor);
    }

    @Override
    public void applyEffectTick(LivingEntity entityLivingBaseIn, int amplifier) {
        if (entityLivingBaseIn.getHealth() > 1f || amplifier >= DEADLY_AMPLIFIER) {
            entityLivingBaseIn.hurt(DamageSource.MAGIC, amplifier + 1);
        }
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        int j = 25 >> amplifier;
        if (j > 0)
            return duration % j == 0;
        else
            return true;
    }
}

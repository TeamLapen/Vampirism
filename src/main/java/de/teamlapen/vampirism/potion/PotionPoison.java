package de.teamlapen.vampirism.potion;

import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.EffectType;
import net.minecraft.util.DamageSource;


public class PotionPoison extends VampirismEffect {

    public PotionPoison(String name, int potionColor) {
        super(name, EffectType.HARMFUL, potionColor);
    }

    @Override
    public boolean isReady(int duration, int amplifier) {
        int j = 25 >> amplifier;
        if (j > 0)
            return duration % j == 0;
        else
            return true;
    }

    @Override
    public void performEffect(LivingEntity entityLivingBaseIn, int amplifier) {
        if (entityLivingBaseIn.getHealth() > 1f) {
            entityLivingBaseIn.attackEntityFrom(DamageSource.MAGIC, 1.0f);
        }
    }
}

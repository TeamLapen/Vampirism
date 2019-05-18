package de.teamlapen.vampirism.potion;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;

public class PotionPoison extends VampirismPotion {

    public PotionPoison(String name, boolean badEffect, int potionColor) {
        super(name, badEffect, potionColor);
        setIconIndex(7, 0);
    }

    @Override
    public void performEffect(EntityLivingBase entityLivingBaseIn, int amplifier) {
        if (entityLivingBaseIn.getHealth() > 1.0F) {
            entityLivingBaseIn.attackEntityFrom(DamageSource.MAGIC, 1.0F);
        }
    }

    @Override
    public boolean isReady(int duration, int amplifier) {
        int j = 25 >> amplifier;
        if (j > 0)
            return duration % j == 0;
        else
            return true;
    }
}

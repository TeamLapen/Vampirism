package de.teamlapen.vampirism.potion;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;

import java.util.ArrayList;

/**
 * Night vision effect for vampire players which is not displayed
 */
public class VampireNightVisionEffect extends PotionEffect {

    public VampireNightVisionEffect() {
        super(MobEffects.NIGHT_VISION, 10000, 0, false, false);
        setCurativeItems(new ArrayList<>());
    }

    @Override
    public boolean combine(PotionEffect other) {
        //Don't change anything
        return false;
    }

    @Override
    public boolean equals(Object p_equals_1_) {
        return p_equals_1_ == this;
    }

    @Override
    public String getEffectName() {
        return "effect.vampirism.nightVision";
    }

    @Override
    public boolean getIsPotionDurationMax() {
        return true;
    }

    @Override
    public boolean tick(EntityLivingBase entityIn) {
        return true;
    }

    @Override
    public void performEffect(EntityLivingBase entityIn) {
    }

    @Override
    public NBTTagCompound write(NBTTagCompound nbt) {
        return nbt;
    }
}

package de.teamlapen.vampirism.potion;

import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;

import java.util.ArrayList;

/**
 * Night vision effect for vampire players which is not displayed
 */
public class VampireNightVisionEffect extends EffectInstance {

    public VampireNightVisionEffect() {
        super(Effects.NIGHT_VISION, 10000, 0, false, false);
        setCurativeItems(new ArrayList<>());
    }

    @Override
    public boolean combine(EffectInstance other) {
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
    public void performEffect(LivingEntity entityIn) {
    }

    @Override
    public boolean tick(LivingEntity entityIn) {
        return true;
    }

    @Override
    public CompoundNBT write(CompoundNBT nbt) {
        return nbt;
    }
}

package de.teamlapen.vampirism.effects;

import de.teamlapen.vampirism.api.effects.IHiddenEffectInstance;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;

import java.util.ArrayList;

/**
 * Night vision effect for vampire players which is not displayed
 */
public class VampireNightVisionEffectInstance extends EffectInstance implements IHiddenEffectInstance {

    public VampireNightVisionEffectInstance() {
        super(Effects.NIGHT_VISION, 10000, 0, false, false);
        setCurativeItems(new ArrayList<>());
    }

    @Override
    public void applyEffect(LivingEntity entityIn) {
    }

    @Override
    public boolean equals(Object p_equals_1_) {
        return p_equals_1_ == this;
    }

    @Override
    public String getDescriptionId() {
        return "effect.vampirism.nightVision";
    }

    @Override
    public boolean isNoCounter() {
        return true;
    }

    @Override
    public CompoundNBT save(CompoundNBT nbt) {
        return nbt;
    }

    @Override
    public boolean tick(LivingEntity entityIn, Runnable p_76455_2_) {
        return true;
    }

    @Override
    public boolean update(EffectInstance other) {
        //Don't change anything
        return false;
    }
}

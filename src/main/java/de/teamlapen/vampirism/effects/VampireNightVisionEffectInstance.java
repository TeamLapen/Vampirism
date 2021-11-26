package de.teamlapen.vampirism.effects;

import de.teamlapen.vampirism.api.effects.IHiddenEffectInstance;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nonnull;
import java.util.ArrayList;

/**
 * Night vision effect for vampire players which is not displayed
 */
public class VampireNightVisionEffectInstance extends MobEffectInstance implements IHiddenEffectInstance{

    public VampireNightVisionEffectInstance() {
        super(MobEffects.NIGHT_VISION, 10000, 0, false, false);
        setCurativeItems(new ArrayList<>());
    }

    @Override
    public void applyEffect(@Nonnull LivingEntity entityIn) {
    }

    @Override
    public boolean equals(Object p_equals_1_) {
        return p_equals_1_ == this;
    }

    @Nonnull
    @Override
    public String getDescriptionId() {
        return "effect.vampirism.nightVision";
    }

    @Override
    public boolean isNoCounter() {
        return true;
    }

    @Nonnull
    @Override
    public CompoundTag save(@Nonnull CompoundTag nbt) {
        return nbt;
    }

    @Override
    public boolean tick(@Nonnull LivingEntity entityIn, @Nonnull Runnable p_76455_2_) {
        return true;
    }

    @Override
    public boolean update(@Nonnull MobEffectInstance other) {
        //Don't change anything
        return false;
    }
}

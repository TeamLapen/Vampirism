package de.teamlapen.vampirism.effects;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.effect.EffectInstanceWithSource;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

/**
 * Night vision effect for vampire players which is not displayed
 */
public class VampireNightVisionEffectInstance extends MobEffectInstance {

    public VampireNightVisionEffectInstance() {
        super(MobEffects.NIGHT_VISION, -1, 0, false, false, false);
        getCures().clear();
        ((EffectInstanceWithSource) this).setSource(VReference.PERMANENT_INVISIBLE_MOB_EFFECT);
    }

    @Override
    public boolean equals(Object object) {
        return object == this;
    }

    @NotNull
    @Override
    public CompoundTag save(@NotNull CompoundTag nbt) {
        return nbt;
    }

    @Override
    public boolean tick(@NotNull LivingEntity entityIn, @NotNull Runnable p_76455_2_) {
        return true;
    }

    @Override
    public boolean update(@NotNull MobEffectInstance other) {
        //Don't change anything
        return false;
    }
}

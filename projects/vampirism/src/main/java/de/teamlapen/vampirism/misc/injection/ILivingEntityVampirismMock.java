package de.teamlapen.vampirism.misc.injection;

import de.teamlapen.vampirism.misc.extension.ILivingEntity;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;

import java.util.Map;

@Deprecated
public interface ILivingEntityVampirismMock extends ILivingEntity {
    @Override
    default Map<Holder<MobEffect>, MobEffectInstance> vampirism$activeEffects() {
        throw new IllegalStateException("This class is only supported as injection class");
    }
}

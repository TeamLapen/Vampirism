package de.teamlapen.vampirism.misc.injection;

import de.teamlapen.vampirism.misc.extension.ILivingEntity;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;

import java.util.Map;

public interface ILivingEntityMock extends ILivingEntity {
    @Override
    default Map<Holder<MobEffect>, MobEffectInstance> getActiveEffects() {
        return Map.of();
    }
}

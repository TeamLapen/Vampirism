package de.teamlapen.vampirism.mixin.accessor;

import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Map;

@Mixin(LivingEntity.class)
public interface LivingEntityAccessor {
    @Invoker("getExperienceReward")
    int invoke_getExperiencePoints_vampirism();

    @Accessor("activeEffects")
    Map<Holder<MobEffect>, MobEffectInstance> getActiveEffects();
}

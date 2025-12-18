package de.teamlapen.vampirism.misc.extension;

import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;

import java.util.Map;

public interface ILivingEntity {

    Map<Holder<MobEffect>, MobEffectInstance> vampirism$activeEffects();

}

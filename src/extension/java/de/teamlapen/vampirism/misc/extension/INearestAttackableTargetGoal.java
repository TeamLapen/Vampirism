package de.teamlapen.vampirism.misc.extension;

import net.minecraft.world.entity.LivingEntity;

public interface INearestAttackableTargetGoal<T extends LivingEntity> {
    Class<T> getTargetType();

}

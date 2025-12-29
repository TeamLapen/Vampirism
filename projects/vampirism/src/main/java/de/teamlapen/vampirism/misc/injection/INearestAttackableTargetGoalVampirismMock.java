package de.teamlapen.vampirism.misc.injection;

import de.teamlapen.vampirism.misc.extension.INearestAttackableTargetGoal;
import net.minecraft.world.entity.LivingEntity;

@Deprecated
public interface INearestAttackableTargetGoalVampirismMock<T extends LivingEntity> extends INearestAttackableTargetGoal<T> {
    @Override
    default Class<T> getTargetType() {
        throw new IllegalStateException("This class is only supported as injection class");
    }
}

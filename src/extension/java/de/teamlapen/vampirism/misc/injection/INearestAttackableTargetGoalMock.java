package de.teamlapen.vampirism.misc.injection;

import de.teamlapen.vampirism.misc.extension.INearestAttackableTargetGoal;
import net.minecraft.world.entity.LivingEntity;

public interface INearestAttackableTargetGoalMock<T extends LivingEntity> extends INearestAttackableTargetGoal<T> {
    @Override
    default Class<T> getTargetType() {
        return null;
    }
}

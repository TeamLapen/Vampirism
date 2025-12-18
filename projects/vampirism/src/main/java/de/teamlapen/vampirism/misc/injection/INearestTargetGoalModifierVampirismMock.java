package de.teamlapen.vampirism.misc.injection;

import de.teamlapen.vampirism.misc.extension.INearestTargetGoal;
import net.minecraft.world.entity.Mob;

@Deprecated
public interface INearestTargetGoalModifierVampirismMock extends INearestTargetGoal {
    @Override
    default void vampirism$ignoreVampires(Mob mob) {
        throw new IllegalStateException("This class is only supported as injection class");
    }

    @Override
    default void vampirism$ignoreFactionEntities() {
        throw new IllegalStateException("This class is only supported as injection class");
    }

    @Override
    default void vampirism$ignoreLineOfSight() {
        throw new IllegalStateException("This class is only supported as injection class");
    }
}

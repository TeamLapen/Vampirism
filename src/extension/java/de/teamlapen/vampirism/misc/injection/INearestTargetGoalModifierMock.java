package de.teamlapen.vampirism.misc.injection;

import de.teamlapen.vampirism.misc.extension.INearestTargetGoal;
import net.minecraft.world.entity.Mob;

public interface INearestTargetGoalModifierMock extends INearestTargetGoal {
    @Override
    default void vampirism$ignoreVampires(Mob mob) {

    }

    @Override
    default void vampirism$ignoreFactionEntities() {

    }

    @Override
    default void vampirism$ignoreLineOfSight() {

    }
}

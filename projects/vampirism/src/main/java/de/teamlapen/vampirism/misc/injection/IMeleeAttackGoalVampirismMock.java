package de.teamlapen.vampirism.misc.injection;

import de.teamlapen.vampirism.misc.extension.IMeleeAttackGoal;
import net.minecraft.world.level.pathfinder.Path;

@Deprecated
public interface IMeleeAttackGoalVampirismMock extends IMeleeAttackGoal {
    @Override
    default Path getPath() {
        throw new IllegalStateException("This class is only supported as injection class");
    }
}

package de.teamlapen.vampirism.misc.injection;

import de.teamlapen.vampirism.misc.extension.IMeleeAttackGoal;
import net.minecraft.world.level.pathfinder.Path;

public interface IMeleeAttackGoalMock extends IMeleeAttackGoal {
    @Override
    default Path getPath() {
        return null;
    }
}

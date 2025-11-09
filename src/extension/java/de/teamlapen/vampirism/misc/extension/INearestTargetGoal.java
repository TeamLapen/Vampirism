package de.teamlapen.vampirism.misc.extension;

import net.minecraft.world.entity.Mob;

public interface INearestTargetGoal {

    void vampirism$ignoreVampires(Mob mob);

    void vampirism$ignoreFactionEntities();

    void vampirism$ignoreLineOfSight();
}

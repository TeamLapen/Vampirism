package de.teamlapen.vampirism.common.entity.ai.goals;

import net.minecraft.world.entity.Mob;

public interface NearestTargetGoalModifier {

    void vampirism$ignoreVampires(Mob mob);

    void vampirism$ignoreFactionEntities();

    void vampirism$ignoreLineOfSight();
}

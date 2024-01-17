package de.teamlapen.vampirism.entity.ai.goals;

import net.minecraft.world.entity.Mob;

public interface NearestTargetGoalModifier {

    void ignoreVampires(Mob mob);

    void ignoreFactionEntities();

    void ignoreLineOfSight();
}

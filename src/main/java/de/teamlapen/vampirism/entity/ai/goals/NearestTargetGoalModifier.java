package de.teamlapen.vampirism.entity.ai.goals;

public interface NearestTargetGoalModifier {

    void ignoreVampires();

    void ignoreFactionEntities();

    void ignoreLineOfSight();
}

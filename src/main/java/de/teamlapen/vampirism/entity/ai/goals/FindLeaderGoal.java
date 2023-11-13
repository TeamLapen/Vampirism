package de.teamlapen.vampirism.entity.ai.goals;

import de.teamlapen.vampirism.api.entity.IEntityLeader;
import de.teamlapen.vampirism.entity.IEntityFollower;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.phys.AABB;

import java.util.function.Predicate;

public class FindLeaderGoal<T extends Mob & IEntityFollower, Z extends LivingEntity & IEntityLeader> extends Goal {

    private final T entity;
    protected TargetingConditions targetConditions;
    private Z leader;
    protected final int randomInterval = reducedTickDelay(10);

    public FindLeaderGoal(T entity, Predicate<Z> leaderPredicate) {
        this.entity = entity;
        this.targetConditions = TargetingConditions.forNonCombat().ignoreLineOfSight().range(this.getFollowDistance()).selector((Predicate<LivingEntity>)(Object) leaderPredicate);
    }

    protected double getFollowDistance() {
        return this.entity.getAttributeValue(Attributes.FOLLOW_RANGE);
    }

    @Override
    public boolean canUse() {
        if ((this.entity.getLeader() != null && this.entity.getLeader().isAlive()) || (this.randomInterval > 0 && this.entity.getRandom().nextInt(this.randomInterval) != 0)) {
            return false;
        } else {
            this.findLeader();
            return this.leader != null;
        }
    }

    @Override
    public void start() {
        this.entity.setLeader(this.leader);
        super.start();
    }

    protected AABB getTargetSearchArea(double pTargetDistance) {
        return this.entity.getBoundingBox().inflate(pTargetDistance, 4.0D, pTargetDistance);
    }

    protected void findLeader() {
        this.leader = (Z) this.entity.level().getNearestEntity(this.entity.level().getEntitiesOfClass(LivingEntity.class, getTargetSearchArea(getFollowDistance()), IEntityLeader.class::isInstance), this.targetConditions, this.entity, this.entity.getX(), this.entity.getY(), this.entity.getZ());
    }
}

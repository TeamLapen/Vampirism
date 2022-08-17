package de.teamlapen.vampirism.entity.goals;

import de.teamlapen.vampirism.api.entity.IEntityLeader;
import de.teamlapen.vampirism.entity.vampire.BasicVampireEntity;
import de.teamlapen.vampirism.entity.vampire.VampireBaseEntity;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;
import java.util.List;

/**
 * Makes the basic hunter follow a nearby advanced vampires
 */
public class FollowAdvancedVampireGoal extends Goal {

    protected final BasicVampireEntity entity;
    protected final double speed;
    /**
     * Maximum distance before the entity starts following the advanced vampire
     */
    private final int DIST = 20;
    private int delayCounter;

    public FollowAdvancedVampireGoal(BasicVampireEntity entity, double speed) {
        this.entity = entity;
        this.speed = speed;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canContinueToUse() {
        if (this.entity.getAdvancedLeader() == null) {
            return false;
        } else {
            double d0 = this.entity.distanceToSqr(this.entity.getAdvancedLeader().getRepresentingEntity());
            return d0 >= DIST && d0 <= 256.0D;
        }
    }

    @Override
    public boolean canUse() {

        IEntityLeader leader = entity.getAdvancedLeader();
        if (leader != null) {
            return leader.getRepresentingEntity().isAlive() && this.entity.distanceToSqr(leader.getRepresentingEntity()) > DIST;
        }

        List<VampireBaseEntity> list = this.entity.getCommandSenderWorld().getEntitiesOfClass(VampireBaseEntity.class, this.entity.getBoundingBox().inflate(8, 4, 8), IEntityLeader.class::isInstance);


        double d0 = Double.MAX_VALUE;

        for (VampireBaseEntity entity1 : list) {
            IEntityLeader leader1 = ((IEntityLeader) entity1);
            if (entity1.isAlive() && leader1.getFollowingCount() < leader1.getMaxFollowerCount()) {
                double d1 = this.entity.distanceToSqr(entity1);

                if (d1 <= d0) {
                    d0 = d1;
                    leader = leader1;
                }
            }
        }

        if (leader == null) {
            return false;
        } else {
            entity.setAdvancedLeader(leader);
            leader.increaseFollowerCount();
            return this.entity.distanceToSqr(leader.getRepresentingEntity()) > DIST;
        }
    }

    @Override
    public void start() {
        delayCounter = 0;
    }

    @Override
    public void tick() {
        if (--this.delayCounter <= 0 && entity.getAdvancedLeader() != null) {
            this.delayCounter = 10;
            this.entity.getNavigation().moveTo(this.entity.getAdvancedLeader().getRepresentingEntity(), this.speed);
            this.entity.lookAt(EntityAnchorArgument.Anchor.EYES, this.entity.getAdvancedLeader().getRepresentingEntity().position().add(0, this.entity.getAdvancedLeader().getRepresentingEntity().getEyeHeight(), 0));
        }
    }
}

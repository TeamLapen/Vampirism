package de.teamlapen.vampirism.entity.ai;

import de.teamlapen.vampirism.entity.vampire.EntityAdvancedVampire;
import de.teamlapen.vampirism.entity.vampire.EntityBasicVampire;
import net.minecraft.entity.ai.EntityAIBase;

import java.util.List;

/**
 * Makes the basic hunter follow a nearby advanced vampires
 */
public class VampireAIFollowAdvanced extends EntityAIBase {

    protected final EntityBasicVampire entity;
    protected final double speed;
    /**
     * Maximum distance before the entity starts following the advanced vampire
     */
    private final int DIST = 20;
    private int delayCounter;

    public VampireAIFollowAdvanced(EntityBasicVampire entity, double speed) {
        this.entity = entity;
        this.speed = speed;
        this.setMutexBits(3);
    }

    @Override
    public boolean shouldContinueExecuting() {
        if (this.entity.getAdvancedLeader() == null) {
            return false;
        } else {
            double d0 = this.entity.getDistanceSq(this.entity.getAdvancedLeader());
            return d0 >= DIST && d0 <= 256.0D;
        }
    }

    @Override
    public boolean shouldExecute() {

        EntityAdvancedVampire leader = entity.getAdvancedLeader();
        if (leader != null) {
            return leader.isEntityAlive() && this.entity.getDistanceSq(leader) > DIST;
        }

        List<EntityAdvancedVampire> list = this.entity.getEntityWorld().getEntitiesWithinAABB(EntityAdvancedVampire.class, this.entity.getEntityBoundingBox().grow(8, 4, 8));

        double d0 = Double.MAX_VALUE;

        for (EntityAdvancedVampire entity1 : list) {
            if (entity1.isEntityAlive() && entity1.getFollowingCount() < entity1.getMaxFollowerCount()) {
                double d1 = this.entity.getDistanceSq(entity1);

                if (d1 <= d0) {
                    d0 = d1;
                    leader = entity1;
                }
            }
        }

        if (leader == null) return false;
        else {
            entity.setAdvancedLeader(leader);
            leader.increaseFollowerCount();
            return this.entity.getDistanceSq(leader) > DIST;
        }
    }

    @Override
    public void startExecuting() {
        delayCounter = 0;
    }

    @Override
    public void updateTask() {
        if (--this.delayCounter <= 0 && entity.getAdvancedLeader() != null) {
            this.delayCounter = 10;
            this.entity.getNavigator().tryMoveToEntityLiving(this.entity.getAdvancedLeader(), this.speed);
            this.entity.getLookHelper().setLookPosition(this.entity.getAdvancedLeader().posX, this.entity.getAdvancedLeader().posY + (double) this.entity.getAdvancedLeader().getEyeHeight(), this.entity.getAdvancedLeader().posZ, (float) this.entity.getHorizontalFaceSpeed(), (float) this.entity.getVerticalFaceSpeed());
        }
    }
}

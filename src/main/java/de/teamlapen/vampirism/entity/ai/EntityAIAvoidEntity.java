package de.teamlapen.vampirism.entity.ai;

import de.teamlapen.vampirism.entity.EntityVampirism;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.Vec3;

import java.util.List;

/**
 * No use atm, simply use vanilla avoid entity
 */
@Deprecated
public class EntityAIAvoidEntity extends EntityAIBase {


    private final IEntitySelector entitySelector = new IEntitySelector() {
        private static final String __OBFID = "CL_00001575";

        /**
         * Return whether the specified entity is applicable to this filter.
         */
        public boolean isEntityApplicable(Entity p_82704_1_) {
            return p_82704_1_.isEntityAlive() && EntityAIAvoidEntity.this.entity.getEntitySenses().canSee(p_82704_1_);
        }
    };
    final EntityVampirism entity;
    private double farSpeed;
    private double nearSpeed;
    private Entity closestLivingEntity;
    private float distanceFromEntity;
    /**
     * The PathEntity of our entity
     */
    private PathEntity entityPathEntity;
    /**
     * The PathNavigate of our entity
     */
    private PathNavigate entityPathNavigate;
    /**
     * The class of the entity we should avoid
     */
    private Class targetEntityClass;

    public EntityAIAvoidEntity(EntityVampirism entity, Class targetEntityClass, float distanceFromEntity, double farSpeed, double nearSpeed) {
        this.entity = entity;
        this.farSpeed = farSpeed;
        this.nearSpeed = nearSpeed;
        this.targetEntityClass = targetEntityClass;
        this.distanceFromEntity = distanceFromEntity;
        this.entityPathNavigate = entity.getNavigator();
        this.setMutexBits(1);
    }

    @Override
    public boolean shouldExecute() {
        if (this.targetEntityClass == EntityPlayer.class) {
            this.closestLivingEntity = this.entity.worldObj.getClosestPlayerToEntity(this.entity, this.distanceFromEntity);
            if (this.closestLivingEntity == null) return false;
        } else {
            List list = this.entity.worldObj.selectEntitiesWithinAABB(this.targetEntityClass, this.entity.boundingBox.expand((double) this.distanceFromEntity, 3.0D, (double) this.distanceFromEntity), this.entitySelector);

            if (list.isEmpty()) {
                return false;
            }

            this.closestLivingEntity = (Entity) list.get(0);
        }


        Vec3 vec3 = RandomPositionGenerator.findRandomTargetBlockAwayFrom(this.entity, 16, 7, Vec3.createVectorHelper(this.closestLivingEntity.posX, this.closestLivingEntity.posY, this.closestLivingEntity.posZ));

        if (vec3 == null) {
            return false;
        } else if (this.closestLivingEntity.getDistanceSq(vec3.xCoord, vec3.yCoord, vec3.zCoord) < this.closestLivingEntity.getDistanceSqToEntity(this.entity)) {
            return false;
        } else {
            this.entityPathEntity = this.entityPathNavigate.getPathToXYZ(vec3.xCoord, vec3.yCoord, vec3.zCoord);
            return this.entityPathEntity == null ? false : this.entityPathEntity.isDestinationSame(vec3);
        }
    }
}

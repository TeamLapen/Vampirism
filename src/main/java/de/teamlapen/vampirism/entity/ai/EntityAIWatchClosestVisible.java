package de.teamlapen.vampirism.entity.ai;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Same as vanilla but ignoring invisible entities
 */
public class EntityAIWatchClosestVisible extends EntityAIWatchClosest {
    public EntityAIWatchClosestVisible(EntityLiving entityIn, Class<? extends Entity> watchTargetClass, float maxDistance) {
        super(entityIn, watchTargetClass, maxDistance);
    }

    public EntityAIWatchClosestVisible(EntityLiving entityIn, Class<? extends Entity> watchTargetClass, float maxDistance, float chanceIn) {
        super(entityIn, watchTargetClass, maxDistance, chanceIn);
    }

    @Override
    public boolean shouldExecute() {
        if (super.shouldExecute()) {
            if (this.closestEntity != null && !this.closestEntity.isInvisible() && !(this.closestEntity instanceof EntityPlayer && ((EntityPlayer) this.closestEntity).capabilities.isCreativeMode)) {
                return true;
            } else {
                this.closestEntity = null;
            }
        }
        return false;
    }
}

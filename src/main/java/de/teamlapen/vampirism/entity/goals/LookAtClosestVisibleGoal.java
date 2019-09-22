package de.teamlapen.vampirism.entity.goals;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.player.PlayerEntity;

/**
 * Same as vanilla but ignoring invisible entities
 */
public class LookAtClosestVisibleGoal extends LookAtGoal {
    public LookAtClosestVisibleGoal(MobEntity entityIn, Class<? extends LivingEntity> watchTargetClass, float maxDistance) {
        super(entityIn, watchTargetClass, maxDistance);
    }

    public LookAtClosestVisibleGoal(MobEntity entityIn, Class<? extends LivingEntity> watchTargetClass, float maxDistance, float chanceIn) {
        super(entityIn, watchTargetClass, maxDistance, chanceIn);
    }

    @Override
    public boolean shouldExecute() {
        if (super.shouldExecute()) {
            if (this.closestEntity != null && !this.closestEntity.isInvisible() && !(this.closestEntity instanceof PlayerEntity && ((PlayerEntity) this.closestEntity).abilities.isCreativeMode)) {
                return true;
            } else {
                this.closestEntity = null;
            }
        }
        return false;
    }
}
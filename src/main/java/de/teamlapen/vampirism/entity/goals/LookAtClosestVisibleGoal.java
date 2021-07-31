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
    public boolean canUse() {
        if (super.canUse()) {
            if (this.lookAt != null && !this.lookAt.isInvisible() && !(this.lookAt instanceof PlayerEntity && ((PlayerEntity) this.lookAt).abilities.instabuild)) {
                return true;
            } else {
                this.lookAt = null;
            }
        }
        return false;
    }
}
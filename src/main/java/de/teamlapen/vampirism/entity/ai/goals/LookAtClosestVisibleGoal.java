package de.teamlapen.vampirism.entity.ai.goals;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Same as vanilla but ignoring invisible entities
 */
public class LookAtClosestVisibleGoal extends LookAtPlayerGoal {
    public LookAtClosestVisibleGoal(@NotNull Mob entityIn, @NotNull Class<? extends LivingEntity> watchTargetClass, float maxDistance) {
        super(entityIn, watchTargetClass, maxDistance);
    }

    public LookAtClosestVisibleGoal(@NotNull Mob entityIn, @NotNull Class<? extends LivingEntity> watchTargetClass, float maxDistance, float chanceIn) {
        super(entityIn, watchTargetClass, maxDistance, chanceIn);
    }

    @Override
    public boolean canUse() {
        if (super.canUse()) {
            if (this.lookAt != null && !this.lookAt.isInvisible() && !(this.lookAt instanceof Player && ((Player) this.lookAt).getAbilities().instabuild)) {
                return true;
            } else {
                this.lookAt = null;
            }
        }
        return false;
    }
}
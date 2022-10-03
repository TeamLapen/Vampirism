package de.teamlapen.vampirism.entity.ai.goals;

import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.IVillageCaptureEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class DefendVillageGoal<T extends PathfinderMob & IVillageCaptureEntity> extends TargetGoal {

    private final @NotNull T creature;
    protected final @NotNull TargetingConditions entityPredicate;
    private final double distance = getFollowDistance() * 4;

    public DefendVillageGoal(@NotNull T creature) {
        super(creature, false, false);
        this.creature = creature;
        this.entityPredicate = new TargetingConditions(true) {
            @Override
            public boolean test(@Nullable LivingEntity attackEntity, @NotNull LivingEntity targetEntity) {
                if (creature.getCaptureInfo() != null && creature.getCaptureInfo().shouldForceTargets() && getFollowDistance() > 0) {
                    range(-1.0D);
                } else if (getFollowDistance() < 0) {
                    range(distance);
                }
                return super.test(attackEntity, targetEntity);
            }
        }.selector(VampirismAPI.factionRegistry().getPredicate(creature.getFaction(), false)).ignoreInvisibilityTesting();
    }

    @Override
    public boolean canUse() {
        if (!creature.isDefendingVillage()) return false;
        this.targetMob = this.creature.level.getNearestEntity(LivingEntity.class, entityPredicate, this.mob, this.mob.getX(), this.mob.getY() + (double) this.mob.getEyeHeight(), this.mob.getZ(), creature.getTargetVillageArea());
        return targetMob != null;
    }

    @Override
    public void start() {
        this.creature.setTarget(this.targetMob);
        super.start();
    }
}

package de.teamlapen.vampirism.entity.goals;

import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.IVillageCaptureEntity;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.TargetGoal;

import javax.annotation.Nullable;


public class DefendVillageGoal<T extends CreatureEntity & IVillageCaptureEntity> extends TargetGoal {

    private final T creature;
    protected final EntityPredicate entityPredicate;
    private final double distance = getFollowDistance() * 4;

    public DefendVillageGoal(T creature) {
        super(creature, false, false);
        this.creature = creature;
        this.entityPredicate = new EntityPredicate() {
            @Override
            public boolean test(@Nullable LivingEntity attackEntity, LivingEntity targetEntity) {
                if (creature.getCaptureInfo() != null && creature.getCaptureInfo().shouldForceTargets() && getFollowDistance() > 0) {
                    range(-1.0D);
                } else if (getFollowDistance() < 0) {
                    range(distance);
                }
                return super.test(attackEntity, targetEntity);
            }
        }.selector(VampirismAPI.factionRegistry().getPredicate(creature.getFaction(), false)).allowUnseeable();
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

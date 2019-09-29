package de.teamlapen.vampirism.entity.goals;

import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.IVillageCaptureEntity;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.TargetGoal;

import javax.annotation.Nullable;


public class DefendVillageGoal<T extends CreatureEntity & IVillageCaptureEntity> extends TargetGoal {

    private T creature;
    private EntityPredicate entityPredicate;
    private double distance = getTargetDistance() * 4;

    public DefendVillageGoal(T creature) {
        super(creature, false, false);
        this.creature = creature;
        this.entityPredicate = new EntityPredicate() {
            @Override
            public boolean canTarget(@Nullable LivingEntity p_221015_1_, LivingEntity p_221015_2_) {
                if (creature.getVillageAttributes().shouldForceTargets() && getTargetDistance() > 0) {
                    setDistance(-1.0D);
                } else if (getTargetDistance() < 0) {
                    setDistance(distance);
                }
                return super.canTarget(p_221015_1_, p_221015_2_);
            }
        }.setCustomPredicate(VampirismAPI.factionRegistry().getPredicate(creature.getFaction(), false));
    }

    @Override
    public boolean shouldExecute() {
        if (!creature.isDefendingVillage()) return false;
        this.target = this.creature.world.getClosestEntityWithinAABB(LivingEntity.class, entityPredicate, this.goalOwner, this.goalOwner.posX, this.goalOwner.posY + (double) this.goalOwner.getEyeHeight(), this.goalOwner.posZ, creature.getTargetVillageArea());
        return target != null;
    }

    @Override
    public void startExecuting() {
        this.creature.setAttackTarget(this.target);
        super.startExecuting();
    }
}

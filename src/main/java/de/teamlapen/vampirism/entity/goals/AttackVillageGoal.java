package de.teamlapen.vampirism.entity.goals;

import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.IVillageCaptureEntity;
import de.teamlapen.vampirism.entity.VampirismEntity;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.TargetGoal;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public class AttackVillageGoal<T extends VampirismEntity & IVillageCaptureEntity> extends TargetGoal {

    private final T attacker;
    private EntityPredicate entityPredicate;
    private double distance = getTargetDistance() * 4;

    public AttackVillageGoal(T creature) {
        super(creature, false, false);
        this.attacker = creature;
        this.entityPredicate = new EntityPredicate() {
            @Override
            public boolean canTarget(@Nullable LivingEntity attackEntity, @Nonnull LivingEntity targetEntity) {
                if (attacker.getCaptureInfo() != null && attacker.getCaptureInfo().shouldForceTargets() && getTargetDistance() > 0) {
                    setDistance(-1.0D);
                } else if (getTargetDistance() < 0) {
                    setDistance(distance);
                }
                return super.canTarget(attackEntity, targetEntity);
            }
        }.setCustomPredicate(VampirismAPI.factionRegistry().getPredicate(attacker.getFaction(), false));
    }

    @Override
    public boolean shouldExecute() {
        if (!attacker.isAttackingVillage()) return false;
        if (attacker.getTargetVillageArea() == null) return false;
        this.target = this.attacker.world.getClosestEntityWithinAABB(LivingEntity.class, entityPredicate, this.goalOwner, this.goalOwner.getPosX(), this.goalOwner.getPosY() + (double) this.goalOwner.getEyeHeight(), this.goalOwner.getPosZ(), attacker.getTargetVillageArea());
        return target != null;
    }

    @Override
    public void startExecuting() {
        this.attacker.setAttackTarget(this.target);
        super.startExecuting();
    }
}

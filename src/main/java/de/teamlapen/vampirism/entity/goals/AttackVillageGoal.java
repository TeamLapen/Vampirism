package de.teamlapen.vampirism.entity.goals;

import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.IVillageCaptureEntity;
import de.teamlapen.vampirism.entity.VampirismEntity;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.TargetGoal;


public class AttackVillageGoal<T extends VampirismEntity & IVillageCaptureEntity> extends TargetGoal {

    private final T attacker;
    private LivingEntity targetEntity;
    private EntityPredicate entityPredicate;

    public AttackVillageGoal(T creature) {
        super(creature, false, false);
        this.attacker = creature;
        this.entityPredicate = new EntityPredicate().setDistance(getTargetDistance()).setCustomPredicate(VampirismAPI.factionRegistry().getPredicate(attacker.getFaction(), false));

    }

    @Override
    public boolean shouldExecute() {
        if (!attacker.isAttackingVillage()) {
            return false;
        }
        this.targetEntity = this.attacker.world.getClosestEntityWithinAABB(LivingEntity.class, entityPredicate, this.goalOwner, this.goalOwner.posX, this.goalOwner.posY + (double) this.goalOwner.getEyeHeight(), this.goalOwner.posZ, attacker.getTargetVillageArea());
        return targetEntity != null;
    }

    @Override
    public void startExecuting() {
        this.attacker.setAttackTarget(this.targetEntity);
        super.startExecuting();
    }

    @Override
    protected double getTargetDistance() {
        return super.getTargetDistance() * 4;
    }
}

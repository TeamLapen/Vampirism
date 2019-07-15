package de.teamlapen.vampirism.entity.goals;

import de.teamlapen.vampirism.api.entity.IVillageCaptureEntity;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.ai.goal.TargetGoal;


public class DefendVillageGoal<T extends CreatureEntity & IVillageCaptureEntity> extends TargetGoal {

    private T creature;

    public DefendVillageGoal(T creature) {//TODO 1.14 village
        super(creature, false, false);
        this.creature = creature;
    }

    @Override
    public boolean shouldExecute() {
        if (creature.isAttackingVillage()) return false;
//        IVampirismVillage village = creature.getCurrentFriendlyVillage();
//        if (village == null) return false;
//
//        IFactionEntity aggressor = village.findNearestVillageAggressor(creature);
//        if (aggressor != null) {
//            this.target = aggressor.getRepresentingEntity();
//            return this.isSuitableTarget(this.target, false);
//        }
//        this.target = null;
        return false;
    }


    @Override
    public void startExecuting() {
        this.creature.setAttackTarget(this.target);
    }


    @Override
    protected double getTargetDistance() {
        return super.getTargetDistance() * 4;
    }
}

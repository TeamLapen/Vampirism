package de.teamlapen.vampirism.entity.minion.goals;

import de.teamlapen.vampirism.entity.minion.MinionEntity;
import de.teamlapen.vampirism.entity.minion.management.MinionTasks;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.TargetGoal;

import java.util.EnumSet;


public class DefendLordGoal extends TargetGoal {

    protected final MinionEntity<?> entity;
    private final EntityPredicate predicate;
    private final int maxStartDistSQ = 200;
    private final int maxStopDistSQ = 500;

    public DefendLordGoal(MinionEntity<?> mobIn) {
        super(mobIn, false, false);
        this.setMutexFlags(EnumSet.of(Goal.Flag.TARGET));
        this.entity = mobIn;
        this.predicate = new EntityPredicate().setCustomPredicate(e -> entity.getAttackPredicate(false).test(e) && entity.getLordOpt().map(lp -> lp.getPlayer().getDistanceSq(e) < maxStartDistSQ).orElse(true)).setUseInvisibilityCheck().setDistance(60);

    }

    @Override
    public boolean shouldContinueExecuting() {
        return entity.getCurrentTask().map(d -> d.getTask() == MinionTasks.protect_lord).orElse(false) && super.shouldContinueExecuting() && entity.getLordOpt().map(lp -> lp.getPlayer().getDistanceSq(target) < maxStopDistSQ).orElse(true);
    }

    @Override
    public boolean shouldExecute() {
        return entity.getCurrentTask().map(d -> d.getTask() == MinionTasks.protect_lord).orElse(false) && entity.getLordOpt().map(lp -> {
            LivingEntity attackTarget = lp.getPlayer().getLastAttackedEntity();
            if (isSuitableTarget(attackTarget, predicate)) {
                this.target = attackTarget;
                return true;
            }
            LivingEntity revengeTarget = lp.getPlayer().getRevengeTarget();
            if (isSuitableTarget(revengeTarget, predicate)) {
                this.target = revengeTarget;
                return true;
            }
//            LivingEntity attackingEntity = lp.getPlayer().getAttackingEntity();
//            if(isSuitableTarget(attackingEntity,predicate)){
//                this.target = attackingEntity;
//                return true;
//            }
            return false;
        }).orElse(false);
    }

    @Override
    public void startExecuting() {
        super.startExecuting();
        this.entity.setAttackTarget(target);
    }
}

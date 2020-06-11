package de.teamlapen.vampirism.entity.minion.goals;

import de.teamlapen.vampirism.entity.minion.MinionEntity;
import de.teamlapen.vampirism.entity.minion.management.MinionTask;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.TargetGoal;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import java.util.EnumSet;


public class DefendAreaGoal extends TargetGoal {

    private final MinionEntity<?> entity;
    private final EntityPredicate predicate;
    /**
     * Cache bb as long as {@link DefendAreaGoal#center} is unmodified
     */
    private AxisAlignedBB bb;
    private BlockPos center;

    public DefendAreaGoal(MinionEntity<?> entity) {
        super(entity, false);
        this.entity = entity;
        this.setMutexFlags(EnumSet.of(Goal.Flag.TARGET));
        this.predicate = new EntityPredicate().setCustomPredicate(entity.getAttackPredicate(true)).setUseInvisibilityCheck().setDistance(60);
    }


    @Override
    public boolean shouldExecute() {
        return entity.getCurrentTask().filter(task -> task.type == MinionTask.Type.DEFEND_AREA && ((MinionTask.DefendArea) task).getCenter() != null).map(task -> {
                    BlockPos newCenter = ((MinionTask.DefendArea) task).getCenter();
                    if (bb == null || center == null || !center.equals(newCenter)) {
                        this.bb = new AxisAlignedBB(newCenter).grow(((MinionTask.DefendArea) task).getRadius());
                        this.center = newCenter;
                    }

                    this.target = entity.world.getClosestEntityWithinAABB(LivingEntity.class, predicate, entity, entity.getPosX(), entity.getPosY(), entity.getPosZ(), bb);
                    return this.target != null;
                }
        ).orElse(false);
    }

    @Override
    public void startExecuting() {
        super.startExecuting();
        this.entity.setAttackTarget(this.target);
    }
}

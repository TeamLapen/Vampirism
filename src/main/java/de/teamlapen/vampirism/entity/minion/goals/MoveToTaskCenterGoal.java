package de.teamlapen.vampirism.entity.minion.goals;


import de.teamlapen.vampirism.entity.goals.MoveToPositionGoal;
import de.teamlapen.vampirism.entity.minion.MinionEntity;
import de.teamlapen.vampirism.entity.minion.management.DefendAreaTask;
import de.teamlapen.vampirism.entity.minion.management.MinionTasks;
import de.teamlapen.vampirism.entity.minion.management.StayTask;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3i;

import java.util.Optional;


public class MoveToTaskCenterGoal extends MoveToPositionGoal<MinionEntity<?>> {


    private BlockPos target;


    public MoveToTaskCenterGoal(MinionEntity<?> entity) {
        super(entity, 1, 1, 10, true, false);
    }

    public Optional<BlockPos> getTargetPos() {
        return entity.getCurrentTask().map(desc -> {
            if (desc.getTask() == MinionTasks.DEFEND_AREA.get()) {
                return ((DefendAreaTask.Desc) desc).center;
            } else if (desc.getTask() == MinionTasks.STAY.get()) {
                return ((StayTask.Desc) desc).position;
            }
            return null;
        });


    }

    @Override
    public boolean canUse() {
        return getTargetPos().map(t -> {
            this.target = t;
            return true;
        }).orElse(false) && super.canUse();
    }

    @Override
    public void stop() {
        super.stop();
        this.target = null;
    }

    @Override
    protected Vector3d getLookPosition() {
        return Vector3d.ZERO;
    }

    @Override
    protected Vector3i getTargetPosition() {
        return target;
    }

}

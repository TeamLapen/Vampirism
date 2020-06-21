package de.teamlapen.vampirism.entity.minion.goals;


import de.teamlapen.vampirism.entity.goals.MoveToPositionGoal;
import de.teamlapen.vampirism.entity.minion.MinionEntity;
import de.teamlapen.vampirism.entity.minion.management.DefendAreaTask;
import de.teamlapen.vampirism.entity.minion.management.MinionTasks;
import de.teamlapen.vampirism.entity.minion.management.StayTask;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

import java.util.Optional;


public class MoveToTaskCenterGoal extends MoveToPositionGoal<MinionEntity<?>> {


    private BlockPos target;


    public MoveToTaskCenterGoal(MinionEntity<?> entity) {
        super(entity, 1, 1, 10, true, false);
    }

    public Optional<BlockPos> getTargetPos() {
        return entity.getCurrentTask().map(desc -> {
            if (desc.getTask() == MinionTasks.defend_area) {
                return ((DefendAreaTask.Desc) desc).center;
            } else if (desc.getTask() == MinionTasks.stay) {
                return ((StayTask.Desc) desc).position;
            }
            return null;
        });


    }

    @Override
    public void resetTask() {
        super.resetTask();
        this.target = null;
    }


    @Override
    public boolean shouldExecute() {
        return getTargetPos().map(t -> {
            this.target = t;
            return true;
        }).orElse(false) && super.shouldExecute();
    }

    @Override
    protected Vec3d getLookPosition() {
        return Vec3d.ZERO;
    }

    @Override
    protected Vec3i getTargetPosition() {
        return target;
    }

}

package de.teamlapen.vampirism.entity.minion.goals;

import de.teamlapen.vampirism.api.entity.player.ILordPlayer;
import de.teamlapen.vampirism.entity.goals.MoveToPositionGoal;
import de.teamlapen.vampirism.entity.minion.MinionEntity;
import de.teamlapen.vampirism.entity.minion.management.MinionTasks;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

import java.util.Optional;


public class FollowLordGoal extends MoveToPositionGoal<MinionEntity<?>> {


    private ILordPlayer lord;


    public FollowLordGoal(MinionEntity<?> entity, double followSpeedIn) {
        super(entity, followSpeedIn, 5, 15, true, true);
    }

    @Override
    public void resetTask() {
        super.resetTask();
        this.lord = null;
    }

    @Override
    public boolean shouldContinueExecuting() {
        return super.shouldContinueExecuting() && this.entity.getCurrentTask().filter(task -> task.getTask() == MinionTasks.follow_lord).isPresent();
    }

    @Override
    public boolean shouldExecute() {
        if (!this.entity.getCurrentTask().filter(task -> task.getTask() == MinionTasks.follow_lord).isPresent())
            return false;
        Optional<ILordPlayer> lord = this.entity.getLordOpt();
        if (!lord.isPresent()) {
            return false;
        }
        this.lord = lord.get();
        if (!super.shouldExecute()) {
            this.lord = null;
            return false;
        } else {
            return true;
        }
    }

    @Override
    protected Vec3d getLookPosition() {
        return lord.getPlayer().getEyePosition(1);
    }

    @Override
    protected Vec3i getTargetPosition() {
        return lord.getPlayer().getPosition();
    }

}


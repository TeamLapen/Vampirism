package de.teamlapen.vampirism.entity.minion.goals;

import de.teamlapen.vampirism.api.entity.player.ILordPlayer;
import de.teamlapen.vampirism.entity.goals.MoveToPositionGoal;
import de.teamlapen.vampirism.entity.minion.MinionEntity;
import de.teamlapen.vampirism.entity.minion.management.MinionTasks;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3i;

import java.util.Optional;


public class FollowLordGoal extends MoveToPositionGoal<MinionEntity<?>> {


    private ILordPlayer lord;


    public FollowLordGoal(MinionEntity<?> entity, double followSpeedIn) {
        super(entity, followSpeedIn, 5, 15, true, true);
    }

    @Override
    public boolean canContinueToUse() {
        return super.canContinueToUse() && this.entity.getCurrentTask().filter(task -> task.getTask() == MinionTasks.FOLLOW_LORD.get() || task.getTask() == MinionTasks.PROTECT_LORD.get()).isPresent();
    }

    @Override
    public boolean canUse() {
        if (!this.entity.getCurrentTask().filter(task -> task.getTask() == MinionTasks.FOLLOW_LORD.get() || task.getTask() == MinionTasks.PROTECT_LORD.get()).isPresent())
            return false;
        Optional<ILordPlayer> lord = this.entity.getLordOpt();
        if (!lord.isPresent()) {
            return false;
        }
        this.lord = lord.get();
        if (!super.canUse()) {
            this.lord = null;
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void stop() {
        super.stop();
        this.lord = null;
    }

    @Override
    protected Vector3d getLookPosition() {
        return lord.getPlayer().getEyePosition(1);
    }

    @Override
    protected Vector3i getTargetPosition() {
        return lord.getPlayer().blockPosition();
    }

}


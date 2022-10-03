package de.teamlapen.vampirism.entity.minion.goals;

import de.teamlapen.vampirism.api.entity.player.ILordPlayer;
import de.teamlapen.vampirism.entity.ai.goals.MoveToPositionGoal;
import de.teamlapen.vampirism.entity.minion.MinionEntity;
import de.teamlapen.vampirism.entity.minion.management.MinionTasks;
import net.minecraft.core.Vec3i;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;


public class FollowLordGoal extends MoveToPositionGoal<MinionEntity<?>> {


    private @Nullable ILordPlayer lord;


    public FollowLordGoal(@NotNull MinionEntity<?> entity, double followSpeedIn) {
        super(entity, followSpeedIn, 5, 15, true, true);
    }

    @Override
    public boolean canContinueToUse() {
        return super.canContinueToUse() && this.entity.getCurrentTask().filter(task -> task.getTask() == MinionTasks.FOLLOW_LORD.get() || task.getTask() == MinionTasks.PROTECT_LORD.get()).isPresent();
    }

    @Override
    public boolean canUse() {
        if (this.entity.getCurrentTask().filter(task -> task.getTask() == MinionTasks.FOLLOW_LORD.get() || task.getTask() == MinionTasks.PROTECT_LORD.get()).isEmpty()) {
            return false;
        }
        Optional<ILordPlayer> lord = this.entity.getLordOpt();
        if (lord.isEmpty()) {
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
    protected @NotNull Vec3 getLookPosition() {
        return lord.getPlayer().getEyePosition(1);
    }

    @Override
    protected @NotNull Vec3i getTargetPosition() {
        return lord.getPlayer().blockPosition();
    }

}


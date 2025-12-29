package de.teamlapen.factions.common.world.entities.goals;

import de.teamlapen.factions.api.factions.lord.ILordPlayer;
import de.teamlapen.factions.common.core.FactionMinionTasks;
import de.teamlapen.factions.common.factions.minions.MinionEntity;
import net.minecraft.core.Vec3i;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;


public class FollowLordGoal extends MoveToPositionGoal<MinionEntity<?>> {


    private @Nullable ILordPlayer<?> lord;


    public FollowLordGoal(@NotNull MinionEntity<?> entity, double followSpeedIn) {
        super(entity, followSpeedIn, 5, 15, true, true);
    }

    @Override
    public boolean canContinueToUse() {
        return super.canContinueToUse() && this.entity.getCurrentTask().filter(task -> task.getTask() == FactionMinionTasks.FOLLOW_LORD.get() || task.getTask() == FactionMinionTasks.PROTECT_LORD.get()).isPresent();
    }

    @Override
    public boolean canUse() {
        if (this.entity.getCurrentTask().filter(task -> task.getTask() == FactionMinionTasks.FOLLOW_LORD.get() || task.getTask() == FactionMinionTasks.PROTECT_LORD.get()).isEmpty()) {
            return false;
        }
        Optional<ILordPlayer<?>> lord = this.entity.getLordOpt();
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
        return Optional.ofNullable(lord).map(x -> x.asEntity().getEyePosition(1)).orElseGet(() -> this.entity.getEyePosition(1));
    }

    @Override
    protected @NotNull Vec3i getTargetPosition() {
        return Optional.ofNullable(lord).map(x -> x.asEntity().blockPosition()).orElseGet(this.entity::blockPosition);
    }

}


package de.teamlapen.factions.common.entities.goals;

import de.teamlapen.factions.common.core.FactionMinionTasks;
import de.teamlapen.factions.common.minions.MinionEntity;
import de.teamlapen.factions.common.minions.management.DefendAreaTask;
import de.teamlapen.factions.common.minions.management.StayTask;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;


public class MoveToTaskCenterGoal extends MoveToPositionGoal<MinionEntity<?>> {


    private @Nullable BlockPos target;


    public MoveToTaskCenterGoal(@NotNull MinionEntity<?> entity) {
        super(entity, 1, 1, 10, true, false);
    }

    public @NotNull Optional<BlockPos> getTargetPos() {
        return entity.getCurrentTask().map(desc -> {
            if (desc.getTask() == FactionMinionTasks.DEFEND_AREA.get()) {
                return ((DefendAreaTask.Desc) desc).center;
            } else if (desc.getTask() == FactionMinionTasks.STAY.get()) {
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
    protected @NotNull Vec3 getLookPosition() {
        return Vec3.ZERO;
    }

    @Override
    protected Vec3i getTargetPosition() {
        return target;
    }

}

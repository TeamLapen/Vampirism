package de.teamlapen.vampirism.entity.minion.goals;


import de.teamlapen.vampirism.entity.minion.MinionEntity;
import de.teamlapen.vampirism.entity.minion.management.DefendAreaTask;
import de.teamlapen.vampirism.entity.minion.management.MinionTasks;
import de.teamlapen.vampirism.entity.minion.management.StayTask;
import net.minecraft.block.BlockState;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.pathfinding.FlyingPathNavigator;
import net.minecraft.pathfinding.GroundPathNavigator;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorldReader;

import java.util.EnumSet;
import java.util.Optional;


public class MoveToTaskCenterGoal extends Goal {

    protected final IWorldReader world;
    private final MinionEntity<?> entity;
    private final PathNavigator navigator;
    private BlockPos target;
    private int timeToRecalcPath;
    private float oldWaterCost;

    public MoveToTaskCenterGoal(MinionEntity<?> entity) {
        this.entity = entity;
        this.world = entity.getEntityWorld();
        this.navigator = entity.getNavigator();

        this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE, Flag.JUMP));
        if (!(entity.getNavigator() instanceof GroundPathNavigator) && !(entity.getNavigator() instanceof FlyingPathNavigator)) {
            throw new IllegalArgumentException("Unsupported mob type for MoveToTaskCenterGoal");
        }
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
        this.target = null;
        this.navigator.clearPath();
        this.entity.setPathPriority(PathNodeType.WATER, this.oldWaterCost);
    }

    @Override
    public boolean shouldContinueExecuting() {
        return !this.navigator.noPath() && super.shouldContinueExecuting();
    }

    @Override
    public boolean shouldExecute() {
        return getTargetPos().map(t -> {
            this.target = t;
            return entity.getDistanceSq(target.getX(), target.getY(), target.getZ()) > 4;
        }).orElse(false);
    }

    @Override
    public void startExecuting() {
        this.timeToRecalcPath = 0;
        this.oldWaterCost = this.entity.getPathPriority(PathNodeType.WATER);
        this.entity.setPathPriority(PathNodeType.WATER, 0.0F);
    }


    @Override
    public void tick() {
        if (--this.timeToRecalcPath <= 0) {
            this.timeToRecalcPath = 10;

            boolean flag = this.navigator.tryMoveToXYZ(target.getX(), target.getY(), target.getZ(), 1);
            if (!flag || this.entity.getRNG().nextInt(8) == 0) {
                if (!(this.entity.getDistanceSq(target.getX(), target.getY(), target.getZ()) < 144.0D)) {
                    int i = MathHelper.floor(target.getX()) - 2;
                    int j = MathHelper.floor(target.getZ()) - 2;
                    int k = MathHelper.floor(target.getY());

                    for (int l = 0; l <= 4; ++l) {
                        for (int i1 = 0; i1 <= 4; ++i1) {
                            if ((l < 1 || i1 < 1 || l > 3 || i1 > 3) && this.canTeleportToBlock(new BlockPos(i + l, k - 1, j + i1))) {
                                this.entity.setLocationAndAngles(((float) (i + l) + 0.5F), k, ((float) (j + i1) + 0.5F), this.entity.rotationYaw, this.entity.rotationPitch);
                                this.navigator.clearPath();
                                return;
                            }
                        }
                    }

                }

            }
        }
    }

    protected boolean canTeleportToBlock(BlockPos pos) {
        BlockState blockstate = this.world.getBlockState(pos);
        return blockstate.canEntitySpawn(this.world, pos, this.entity.getType()) && this.world.isAirBlock(pos.up()) && this.world.isAirBlock(pos.up(2));
    }
}

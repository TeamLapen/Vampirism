package de.teamlapen.vampirism.entity.minion.goals;

import de.teamlapen.vampirism.api.entity.player.ILordPlayer;
import de.teamlapen.vampirism.entity.minion.MinionEntity;
import de.teamlapen.vampirism.entity.minion.management.MinionTask;
import net.minecraft.block.BlockState;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.pathfinding.FlyingPathNavigator;
import net.minecraft.pathfinding.GroundPathNavigator;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorldReader;

import java.util.EnumSet;
import java.util.Optional;


public class FollowLordGoal extends Goal {


    protected final MinionEntity<?> entity;
    protected final IWorldReader world;
    private final double followSpeed;
    private final PathNavigator navigator;
    private final float maxDist;
    private final float minDist;
    protected ILordPlayer lord;
    private int timeToRecalcPath;
    private float oldWaterCost;

    public FollowLordGoal(MinionEntity<?> entity, double followSpeedIn, float minDistIn, float maxDistIn) {
        this.entity = entity;
        this.world = entity.world;
        this.followSpeed = followSpeedIn;
        this.navigator = entity.getNavigator();
        this.minDist = minDistIn;
        this.maxDist = maxDistIn;
        this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        if (!(entity.getNavigator() instanceof GroundPathNavigator) && !(entity.getNavigator() instanceof FlyingPathNavigator)) {
            throw new IllegalArgumentException("Unsupported mob type for FollowLordGoal");
        }
    }

    public void resetTask() {
        this.lord = null;
        this.navigator.clearPath();
        this.entity.setPathPriority(PathNodeType.WATER, this.oldWaterCost);
    }

    public boolean shouldContinueExecuting() {
        return !this.navigator.noPath() && this.entity.getDistanceSq(this.lord.getPlayer()) > (double) (this.maxDist * this.maxDist) && this.entity.getCurrentTask().filter(task -> task.type == MinionTask.Type.FOLLOW).isPresent();
    }

    public boolean shouldExecute() {
        if (!this.entity.getCurrentTask().filter(task -> task.type == MinionTask.Type.FOLLOW).isPresent()) return false;
        Optional<ILordPlayer> lord = this.entity.getLordOpt();
        if (!lord.isPresent()) {
            return false;
        } else if (lord.get().getPlayer().getDistanceSq(entity) < (double) (this.minDist * this.minDist)) {
            return false;
        } else {
            this.lord = lord.get();
            return true;
        }
    }

    public void startExecuting() {
        this.timeToRecalcPath = 0;
        this.oldWaterCost = this.entity.getPathPriority(PathNodeType.WATER);
        this.entity.setPathPriority(PathNodeType.WATER, 0.0F);
    }

    public void tick() {
        PlayerEntity player = lord.getPlayer();
        this.entity.getLookController().setLookPositionWithEntity(player, 10.0F, (float) this.entity.getVerticalFaceSpeed());
        if (--this.timeToRecalcPath <= 0) {
            this.timeToRecalcPath = 10;
            boolean flag = this.navigator.tryMoveToEntityLiving(player, this.followSpeed);
            if (!flag || this.entity.getRNG().nextInt(8) == 0) {
                if (!(this.entity.getDistanceSq(player) < 144.0D)) {
                    int i = MathHelper.floor(player.getPosX()) - 2;
                    int j = MathHelper.floor(player.getPosZ()) - 2;
                    int k = MathHelper.floor(player.getBoundingBox().minY);

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


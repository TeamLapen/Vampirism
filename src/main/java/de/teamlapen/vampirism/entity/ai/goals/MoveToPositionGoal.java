package de.teamlapen.vampirism.entity.ai.goals;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;

/**
 * 1.14
 *
 * @author maxanier
 */
public abstract class MoveToPositionGoal<T extends PathfinderMob> extends Goal {


    protected final T entity;
    protected final @NotNull LevelReader world;
    private final double followSpeed;
    private final @NotNull PathNavigation navigator;
    private final float minDist;
    private final float maxDist;
    private final boolean doTeleport;
    private final boolean look;
    private int timeToRecalcPath;
    private float oldWaterCost;

    public MoveToPositionGoal(@NotNull T entity, double followSpeed, float minDist, float maxDist, boolean doTeleport, boolean look) {
        this.entity = entity;
        this.world = entity.getCommandSenderWorld();
        this.followSpeed = followSpeed;
        this.minDist = minDist;
        this.navigator = entity.getNavigation();
        this.maxDist = maxDist;
        this.doTeleport = doTeleport;
        this.look = look;
        this.setFlags(look ? EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK, Flag.JUMP) : EnumSet.of(Goal.Flag.MOVE, Flag.JUMP));
        if (!(entity.getNavigation() instanceof GroundPathNavigation) && !(entity.getNavigation() instanceof FlyingPathNavigation)) {
            throw new IllegalArgumentException("Unsupported mob type for MoveToPositionGoal");
        }
    }

    @Override
    public boolean canContinueToUse() {
        return this.getTargetPosition().distSqr(this.entity.blockPosition()) > this.minDist * minDist;
    }

    @Override
    public boolean canUse() {
        return this.getTargetPosition().distSqr(entity.blockPosition()) > this.minDist * minDist;
    }

    @Override
    public void start() {
        this.timeToRecalcPath = 0;
        this.oldWaterCost = this.entity.getPathfindingMalus(PathType.WATER);
        this.entity.setPathfindingMalus(PathType.WATER, 0.0F);
    }

    @Override
    public void stop() {
        this.navigator.stop();
        this.entity.setPathfindingMalus(PathType.WATER, this.oldWaterCost);
    }

    @Override
    public void tick() {
        Vec3i target = getTargetPosition();
        if (look) this.entity.getLookControl().setLookAt(getLookPosition());
        if (--this.timeToRecalcPath <= 0) {
            this.timeToRecalcPath = 10;
            boolean flag = this.navigator.moveTo(target.getX(), target.getY(), target.getZ(), this.followSpeed);
            if (doTeleport && (!flag || this.entity.getRandom().nextInt(8) == 0)) {
                if (!(this.entity.distanceToSqr(target.getX(), target.getY(), target.getZ()) < maxDist * maxDist)) {
                    int sX = target.getX() - 2;
                    int sZ = target.getZ() - 2;
                    int sY = target.getY();

                    for (int dX = 0; dX <= 4; ++dX) {
                        for (int dZ = 0; dZ <= 4; ++dZ) {
                            if ((dX < 1 || dZ < 1 || dX > 3 || dZ > 3) && this.canTeleportToBlock(new BlockPos(sX + dX, sY - 1, sZ + dZ))) {
                                this.entity.moveTo(((float) (sX + dX) + 0.5F), sY, ((float) (sZ + dZ) + 0.5F), this.entity.getYRot(), this.entity.getXRot());
                                this.navigator.stop();
                                return;
                            }
                        }
                    }

                }

            }
        }

    }

    protected boolean canTeleportToBlock(@NotNull BlockPos pos) {
        BlockState blockstate = this.world.getBlockState(pos);
        return blockstate.isValidSpawn(this.world, pos, this.entity.getType()) && this.world.isEmptyBlock(pos.above()) && this.world.isEmptyBlock(pos.above(2));
    }

    protected abstract Vec3 getLookPosition();

    protected abstract Vec3i getTargetPosition();
}

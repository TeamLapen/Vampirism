package de.teamlapen.vampirism.entity.goals;

import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.EnumSet;
import java.util.Random;

/**
 * Basic Flee from anything ai
 */
public abstract class FleeGoal extends Goal {
    private final CreatureEntity theCreature;
    private final double movementSpeed;
    private final World world;
    private final boolean restrictToHome;
    private double shelterX;
    private double shelterY;
    private double shelterZ;

    public FleeGoal(CreatureEntity theCreature, double movementSpeed, boolean restrictToHome) {
        this.theCreature = theCreature;
        this.movementSpeed = movementSpeed;
        this.restrictToHome = restrictToHome;
        world = theCreature.getEntityWorld();
        this.setMutexFlags(EnumSet.of(Flag.MOVE));
    }

    public boolean continueExecuting() {
        return !this.theCreature.getNavigator().noPath();
    }

    @Override
    public boolean shouldExecute() {
        if (!shouldFlee()) return false;
        Vec3d vec3 = this.findPossibleShelter();

        if (vec3 == null) {
            return false;
        } else {
            this.shelterX = vec3.x;
            this.shelterY = vec3.y;
            this.shelterZ = vec3.z;
            return true;
        }
    }

    public void startExecuting() {
        this.theCreature.getNavigator().tryMoveToXYZ(this.shelterX, this.shelterY, this.shelterZ, this.movementSpeed);
    }

    protected abstract boolean isPositionAcceptable(World world, BlockPos pos);

    protected abstract boolean shouldFlee();

    private Vec3d findPossibleShelter() {
        Random random = this.theCreature.getRNG();
        BlockPos blockpos = new BlockPos(this.theCreature.getPosX(), this.theCreature.getBoundingBox().minY, this.theCreature.getPosZ());

        for (int i = 0; i < 10; ++i) {
            BlockPos blockpos1 = blockpos.add(random.nextInt(20) - 10, random.nextInt(6) - 3, random.nextInt(20) - 10);

            if (isPositionAcceptable(world, blockpos1)) {
                if (restrictToHome && !theCreature.getHomePosition().equals(BlockPos.ZERO)) {

                    if (!theCreature.isWithinHomeDistanceFromPosition(blockpos1)) continue;
                }
                return new Vec3d(blockpos1.getX(), blockpos1.getY(), blockpos1.getZ());
            }
        }

        return null;
    }

}
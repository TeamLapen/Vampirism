package de.teamlapen.vampirism.entity.ai;

import de.teamlapen.vampirism.entity.EntityVampirism;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Random;

/**
 * Basic Flee from anything ai
 */
public abstract class EntityAIFlee extends EntityAIBase {
    private final EntityVampirism theCreature;
    private final double movementSpeed;
    private final World world;
    private final boolean restrictToHome;
    private double shelterX;
    private double shelterY;
    private double shelterZ;

    public EntityAIFlee(EntityVampirism theCreature, double movementSpeed, boolean restrictToHome) {
        this.theCreature = theCreature;
        this.movementSpeed = movementSpeed;
        this.restrictToHome = restrictToHome;
        world = theCreature.worldObj;
        this.setMutexBits(1);
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
            this.shelterX = vec3.xCoord;
            this.shelterY = vec3.yCoord;
            this.shelterZ = vec3.zCoord;
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
        BlockPos blockpos = new BlockPos(this.theCreature.posX, this.theCreature.getEntityBoundingBox().minY, this.theCreature.posZ);

        for (int i = 0; i < 10; ++i) {
            BlockPos blockpos1 = blockpos.add(random.nextInt(20) - 10, random.nextInt(6) - 3, random.nextInt(20) - 10);

            if (isPositionAcceptable(world, blockpos1)) {
                if (restrictToHome && theCreature.hasHome()) {
                    if (!theCreature.isWithinHomeDistance(blockpos1)) continue;
                }
                return new Vec3d((double) blockpos1.getX(), (double) blockpos1.getY(), (double) blockpos1.getZ());
            }
        }

        return null;
    }

}
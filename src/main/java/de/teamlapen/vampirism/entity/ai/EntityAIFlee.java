package de.teamlapen.vampirism.entity.ai;

import de.teamlapen.vampirism.entity.EntityVampirism;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import java.util.Random;

/**
 * Generic abstract flee <something> AI.
 * The subclasses have to specify what to flee.
 *
 * @author Maxanier
 */
public abstract class EntityAIFlee extends EntityAIBase {

    private final EntityVampirism entity;
    private final double speed;
    private final boolean restrictToHome;
    private double shelterX;
    private double shelterY;
    private double shelterZ;

    public EntityAIFlee(EntityVampirism entity, double speed) {
        this(entity, speed, false);
    }

    public EntityAIFlee(EntityVampirism entity, double speed, boolean restrictToHome) {
        this.entity = entity;
        this.speed = speed;
        this.restrictToHome = restrictToHome;

        this.setMutexBits(1);
    }

    @Override
    public boolean continueExecuting() {
        return !this.entity.getNavigator().noPath();
    }

    protected Vec3 findPossibleShelter() {
        Random random = entity.getRNG();

        for (int i = 0; i < 10; ++i) {
            int x = MathHelper.floor_double(entity.posX + random.nextInt(20) - 10.0D);
            int y = MathHelper.floor_double(this.entity.boundingBox.minY + random.nextInt(6) - 3.0D);
            int z = MathHelper.floor_double(this.entity.posZ + random.nextInt(20) - 10.0D);

            if (isPositionAcceptable(entity.worldObj, x, y, z)) {
                if (restrictToHome && entity.hasHome()) {
                    if (!entity.isWithinHomeDistance(x, y, z)) {
                        continue;
                    }
                }
                return Vec3.createVectorHelper(x, y, z);
            }
        }

        return null;
    }

    protected abstract boolean isPositionAcceptable(World world, int x, int y, int z);

    protected abstract boolean shouldFlee();

    @Override
    public boolean shouldExecute() {
        if (shouldFlee()) {
            Vec3 vec3 = this.findPossibleShelter();

            if (vec3 == null) {
                return false;
            } else {
                this.shelterX = vec3.xCoord;
                this.shelterY = vec3.yCoord;
                this.shelterZ = vec3.zCoord;
                return true;
            }
        }
        return false;
    }

    @Override
    public void startExecuting() {
        entity.getNavigator().tryMoveToXYZ(this.shelterX, this.shelterY, this.shelterZ, this.speed);
    }

}

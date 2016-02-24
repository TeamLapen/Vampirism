package de.teamlapen.vampirism.entity.ai;

import de.teamlapen.vampirism.entity.ExtendedCreature;
import de.teamlapen.vampirism.entity.vampire.EntityVampireBase;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;

import java.util.List;


public class VampireAIMoveToBiteable extends EntityAIBase {


    private final EntityVampireBase vampire;
    private final double movementSpeed;
    private EntityCreature target;
    private int timeout;

    public VampireAIMoveToBiteable(EntityVampireBase vampire, double movementSpeed) {
        this.vampire = vampire;
        this.movementSpeed = movementSpeed;
        this.setMutexBits(1);
    }

    @Override
    public boolean continueExecuting() {
        return (!this.vampire.getNavigator().noPath() && !target.isDead);
    }

    @Override
    public void resetTask() {
        target = null;
        timeout = (vampire.getRNG().nextInt(5) == 0 ? 80 : 3);
    }


    @Override
    public boolean shouldExecute() {
        if (timeout > 0) {
            timeout--;
            return false;
        }
        if (!vampire.wantsBlood()) return false;
        List list = vampire.worldObj.getEntitiesWithinAABB(EntityCreature.class, vampire.getEntityBoundingBox().expand(10, 3, 10));
        for (Object o : list) {
            if (ExtendedCreature.get((EntityCreature) o).canBeBitten(vampire)) {
                target = (EntityCreature) o;
                return true;
            }
        }
        target = null;
        return false;
    }

    @Override
    public void startExecuting() {
        vampire.getNavigator().tryMoveToEntityLiving(target, 1.0);
    }
}

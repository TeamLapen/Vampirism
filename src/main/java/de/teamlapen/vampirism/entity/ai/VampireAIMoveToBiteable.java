package de.teamlapen.vampirism.entity.ai;

import de.teamlapen.vampirism.api.entity.IExtendedCreatureVampirism;
import de.teamlapen.vampirism.api.entity.vampire.IVampireMob;
import de.teamlapen.vampirism.entity.ExtendedCreature;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.EntitySelectors;

import java.util.List;


public class VampireAIMoveToBiteable<T extends EntityLiving & IVampireMob> extends EntityAIBase {


    private final T vampire;
    private final double movementSpeed;
    private EntityCreature target;
    private int timeout;

    /**
     * @param vampire       Has to be a {@link EntityLiving}
     * @param movementSpeed
     */
    public VampireAIMoveToBiteable(T vampire, double movementSpeed) {
        this.vampire = vampire;
        this.movementSpeed = movementSpeed;
        this.setMutexBits(1);
    }

    @Override
    public void resetTask() {
        target = null;
        timeout = (vampire.getRNG().nextInt(5) == 0 ? 80 : 3);
    }

    @Override
    public boolean shouldContinueExecuting() {
        return (!this.vampire.getNavigator().noPath() && target.isAlive());
    }

    @Override
    public boolean shouldExecute() {
        if (timeout > 0) {
            timeout--;
            return false;
        }
        if (!vampire.wantsBlood()) return false;
        List<EntityCreature> list = vampire.getEntityWorld().getEntitiesWithinAABB(EntityCreature.class, vampire.getBoundingBox().grow(10, 3, 10), EntitySelectors.NOT_SPECTATING.and((entity) -> entity != vampire && entity.isAlive()));
        for (EntityCreature o : list) {
            IExtendedCreatureVampirism creature = ExtendedCreature.get(o);
            if (creature.canBeBitten(vampire) && !o.hasCustomName() && !creature.hasPoisonousBlood()) {
                target = o;
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

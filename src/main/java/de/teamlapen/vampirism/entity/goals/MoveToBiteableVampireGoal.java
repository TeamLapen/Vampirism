package de.teamlapen.vampirism.entity.goals;

import de.teamlapen.vampirism.api.entity.IExtendedCreatureVampirism;
import de.teamlapen.vampirism.api.entity.vampire.IVampireMob;
import de.teamlapen.vampirism.entity.ExtendedCreature;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.EntityPredicates;

import java.util.EnumSet;
import java.util.List;


public class MoveToBiteableVampireGoal<T extends MobEntity & IVampireMob> extends Goal {


    private final T vampire;
    private final double movementSpeed;
    private CreatureEntity target;
    private int timeout;

    /**
     * @param vampire       Has to be a {@link MobEntity}
     * @param movementSpeed
     */
    public MoveToBiteableVampireGoal(T vampire, double movementSpeed) {
        this.vampire = vampire;
        this.movementSpeed = movementSpeed;
        this.setMutexFlags(EnumSet.of(Flag.MOVE));
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
        List<CreatureEntity> list = vampire.getEntityWorld().getEntitiesWithinAABB(CreatureEntity.class, vampire.getBoundingBox().grow(10, 3, 10), EntityPredicates.NOT_SPECTATING.and((entity) -> entity != vampire && entity.isAlive()));
        for (CreatureEntity o : list) {
            IExtendedCreatureVampirism creature = ExtendedCreature.getUnsafe(o);
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

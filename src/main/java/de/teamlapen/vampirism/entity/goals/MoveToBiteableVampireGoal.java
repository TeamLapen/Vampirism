package de.teamlapen.vampirism.entity.goals;

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
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canContinueToUse() {
        return (!this.vampire.getNavigation().isDone() && target.isAlive());
    }

    @Override
    public boolean canUse() {
        if (timeout > 0) {
            timeout--;
            return false;
        }
        if (!vampire.wantsBlood()) return false;
        List<CreatureEntity> list = vampire.getCommandSenderWorld().getEntitiesOfClass(CreatureEntity.class, vampire.getBoundingBox().inflate(10, 3, 10), EntityPredicates.NO_SPECTATORS.and((entity) -> entity != vampire && entity.isAlive()));
        for (CreatureEntity o : list) {
            if (ExtendedCreature.getSafe(o).map(creature -> creature.canBeBitten(vampire) && !creature.getEntity().hasCustomName() && !creature.hasPoisonousBlood()).orElse(false)) {
                this.target = o;
                return true;
            }
        }
        target = null;
        return false;
    }

    @Override
    public void start() {
        vampire.getNavigation().moveTo(target, 1.0);
    }

    @Override
    public void stop() {
        target = null;
        timeout = (vampire.getRandom().nextInt(5) == 0 ? 80 : 3);
    }
}

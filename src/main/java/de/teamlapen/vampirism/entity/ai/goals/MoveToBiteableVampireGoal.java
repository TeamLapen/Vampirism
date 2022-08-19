package de.teamlapen.vampirism.entity.ai.goals;

import de.teamlapen.vampirism.api.entity.vampire.IVampireMob;
import de.teamlapen.vampirism.entity.ExtendedCreature;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.List;

public class MoveToBiteableVampireGoal<T extends Mob & IVampireMob> extends Goal {

    private final T vampire;
    @SuppressWarnings("FieldCanBeLocal")
    private final double movementSpeed;
    private @Nullable PathfinderMob target;
    private int timeout;

    /**
     * @param vampire Has to be a {@link Mob}
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
        List<PathfinderMob> list = vampire.getCommandSenderWorld().getEntitiesOfClass(PathfinderMob.class, vampire.getBoundingBox().inflate(10, 3, 10), EntitySelector.NO_SPECTATORS.and((entity) -> entity != vampire && entity.isAlive()));
        for (PathfinderMob o : list) {
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

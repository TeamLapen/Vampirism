package de.teamlapen.vampirism.entity.goals;

import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.player.PlayerEntity;

import javax.annotation.Nonnull;
import java.util.EnumSet;
import java.util.Optional;

/**
 * Makes the hunter trainer look at his trainee
 */
public class ForceLookEntityGoal<T extends MobEntity & ForceLookEntityGoal.TaskOwner> extends LookAtGoal {
    private final T theTrainer;

    /**
     * @param theTrainer Has to be instance of ITrainer
     */
    public ForceLookEntityGoal(T theTrainer) {
        super(theTrainer, PlayerEntity.class, 8.0F);
        this.theTrainer = theTrainer;
        this.setMutexFlags(EnumSet.of(Flag.LOOK, Flag.MOVE));
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute() {
        return this.theTrainer.getForceLookTarget().map(t -> this.closestEntity = t).isPresent();
    }


    /**
     * Interface used by {@link ForceLookEntityGoal}
     */
    public interface TaskOwner {
        /**
         * @return The player currently being trained or null
         */
        @Nonnull
        Optional<PlayerEntity> getForceLookTarget();
    }
}

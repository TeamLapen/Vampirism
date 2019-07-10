package de.teamlapen.vampirism.entity.ai;

import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.player.PlayerEntity;

import java.util.EnumSet;

/**
 * Makes the hunter trainer look at his trainee
 */
public class LookAtTrainerHunterGoal<T extends MobEntity & LookAtTrainerHunterGoal.ITrainer> extends LookAtGoal {
    private final T theTrainer;

    /**
     * @param theTrainer Has to be instance of ITrainer
     */
    public LookAtTrainerHunterGoal(T theTrainer) {
        super(theTrainer, PlayerEntity.class, 8.0F);
        this.theTrainer = theTrainer;
        this.setMutexFlags(EnumSet.of(Flag.LOOK));
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute() {
        if (this.theTrainer.getTrainee() != null) {
            this.closestEntity = this.theTrainer.getTrainee();
            return true;
        } else {
            return false;
        }
    }

    /**
     * Interface used by {@link LookAtTrainerHunterGoal}
     */
    public interface ITrainer {
        /**
         * @return The player currently being trained or null
         */
        PlayerEntity getTrainee();
    }
}

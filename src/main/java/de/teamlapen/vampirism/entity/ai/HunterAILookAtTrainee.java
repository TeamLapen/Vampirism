package de.teamlapen.vampirism.entity.ai;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Makes the hunter trainer look at his trainee
 */
public class HunterAILookAtTrainee<T extends EntityLiving & HunterAILookAtTrainee.ITrainer> extends EntityAIWatchClosest {
    private final T theTrainer;

    /**
     * @param theTrainer Has to be instance of ITrainer
     */
    public HunterAILookAtTrainee(T theTrainer) {
        super(theTrainer, EntityPlayer.class, 8.0F);
        this.theTrainer = theTrainer;
        this.setMutexBits(5);
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
     * Interface used by {@link HunterAILookAtTrainee}
     */
    public interface ITrainer {
        /**
         * @return The player currently being trained or null
         */
        EntityPlayer getTrainee();
    }
}

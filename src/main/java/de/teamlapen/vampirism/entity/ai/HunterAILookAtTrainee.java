package de.teamlapen.vampirism.entity.ai;

import de.teamlapen.vampirism.entity.hunter.EntityHunterTrainer;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Makes the hunter trainer look at his trainee
 */
public class HunterAILookAtTrainee extends EntityAIWatchClosest {
    private final EntityHunterTrainer theTrainer;

    public HunterAILookAtTrainee(EntityHunterTrainer theTrainer) {
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
}

package de.teamlapen.vampirism.entity.goals;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nonnull;
import java.util.EnumSet;
import java.util.Optional;

import net.minecraft.world.entity.ai.goal.Goal.Flag;

/**
 * Makes the hunter trainer look at his trainee
 */
public class ForceLookEntityGoal<T extends Mob & ForceLookEntityGoal.TaskOwner> extends LookAtPlayerGoal {
    private final T theTrainer;

    /**
     * @param theTrainer Has to be instance of ITrainer
     */
    public ForceLookEntityGoal(T theTrainer) {
        super(theTrainer, Player.class, 8.0F);
        this.theTrainer = theTrainer;
        this.setFlags(EnumSet.of(Flag.LOOK, Flag.MOVE));
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean canUse() {
        return this.theTrainer.getForceLookTarget().map(t -> this.lookAt = t).isPresent();
    }


    /**
     * Interface used by {@link ForceLookEntityGoal}
     */
    public interface TaskOwner {
        /**
         * @return The player currently being trained or null
         */
        @Nonnull
        Optional<Player> getForceLookTarget();
    }
}

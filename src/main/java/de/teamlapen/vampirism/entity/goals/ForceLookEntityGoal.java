package de.teamlapen.vampirism.entity.goals;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;
import java.util.Optional;

/**
 * Makes the hunter trainer look at his trainee
 */
public class ForceLookEntityGoal<T extends Mob & ForceLookEntityGoal.TaskOwner> extends LookAtPlayerGoal {
    private final @NotNull T theTrainer;

    /**
     * @param theTrainer Has to be {@code instanceof} ITrainer
     */
    public ForceLookEntityGoal(@NotNull T theTrainer) {
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
        @NotNull
        Optional<Player> getForceLookTarget();
    }
}

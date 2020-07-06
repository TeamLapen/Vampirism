package de.teamlapen.vampirism.api.entity.player.task;

import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TaskRequirement {

    private final Requirement<?>[] requirements;

    public TaskRequirement(Requirement<?>[] requirements) {
        this.requirements = requirements;
        for (int i = 0; i < this.requirements.length; i++) {

        }
    }

    public Requirement<?>[] getRequirements() {
        return requirements;
    }

    /**
     * if needed removes the requirements from the player upon task completion
     *
     * @param player the player which completed the task
     */
    public void removeRequirement(IFactionPlayer<?> player) {
    }

    public interface Requirement<T> {
        @Nonnull
        default Type getType() {
            return Type.BOOLEAN;
        }

        /**
         * @param player the player who wants to complete this task
         * @return the stat the needs to be achieved with {@link #getAmount(IFactionPlayer)} to complete the requirement
         * @throws ClassCastException if Object is not applicant for the {@link #getType()}
         */
        @Nonnull
        T getStat(IFactionPlayer<?> player);

        /**
         * @return the needed amount of the {@link #getStat(IFactionPlayer)} to complete this requirement
         */
        default int getAmount(IFactionPlayer<?> player) {
            return 1;
        }

        /**
         * if needed removes the requirements from the player upon task completion
         *
         * @param player the player which completed the task
         */
        default void removeRequirement(IFactionPlayer<?> player) {
        }

        @Nonnull
        ResourceLocation getId();

    }

    @SuppressWarnings("JavadocReference")
    public enum Type {
        /**
         * based on {@link net.minecraft.stats.Stats.CUSTOM} stat increase
         */
        STATS,
        /**
         * based on item in inventory
         */
        ITEMS,
        /**
         * based on {@link net.minecraft.stats.Stats.ENTITY_KILLED} stat increased
         */
        ENTITY,
        /**
         * based on {@link net.minecraft.stats.Stats.ENTITY_KILLED} stat increased, but for multiple entities.
         */
        ENTITY_TAG,
        /**
         * based on boolean supplier
         */
        BOOLEAN
    }

}
